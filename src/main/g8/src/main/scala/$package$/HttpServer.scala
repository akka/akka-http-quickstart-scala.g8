/*
 * Copyright (C) 2009-2019 Lightbend Inc. <https://www.lightbend.com>
 */
package $package$

// #http-server
import akka.actor.typed.Behavior
import akka.actor.typed.PostStop
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.adapter._
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Route

import scala.util.Failure
import scala.util.Success

object HttpServer {

  sealed trait Message
  final private case class ServerStarted(binding: ServerBinding) extends Message
  case object Stop extends Message

  def apply(host: String, port: Int, routes: Route): Behavior[Message] = Behaviors.setup { context =>
    // Akka HTTP still needs a classic ActorSystem to start
    implicit val classicSystem: akka.actor.ActorSystem = context.system.toClassic

    val futureBinding = Http().bindAndHandle(routes, host, port)
    context.pipeToSelf(futureBinding) {
      case Success(binding) => ServerStarted(binding)
      case Failure(ex) => throw new RuntimeException("Failed to start server", ex)
    }

    def starting(): Behavior[Message] = Behaviors.withStash(1) { stash =>
      Behaviors.receiveMessage {
        case ServerStarted(binding) =>
          context.log.info(
            s"Server online at http://{}:{}/",
            binding.localAddress.getHostString,
            binding.localAddress.getPort)
          stash.unstashAll(running(binding))
      }
    }

    def running(binding: ServerBinding): Behavior[Message] =
      Behaviors.receiveMessagePartial[Message] {
        case Stop =>
          Behaviors.stopped
      }.receiveSignal {
        case (_, PostStop) =>
          binding.unbind()
          Behaviors.same
      }

    // initial behavior is waiting for start to complete
    starting()
  }
}
// #http-server