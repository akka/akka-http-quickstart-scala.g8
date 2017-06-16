package com.lightbend.akka.http.sample

import akka.actor.{ ActorRef, ActorSystem }
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{ ExceptionHandler, Route }
import akka.http.scaladsl.server.directives.MethodDirectives.delete
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.MethodDirectives.post
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.directives.PathDirectives.path

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.io.StdIn
import scala.util.{ Failure, Success }
import com.lightbend.akka.http.sample.UserRegistryActor._

//#main-class
object QuickstartServer extends App with JsonSupport {
  //#main-class
  //#server-bootstrapping
  implicit val system: ActorSystem = ActorSystem("helloAkkaHttpServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  //#server-bootstrapping

  // Needed for the Future and its methods flatMap/onComplete in the end
  implicit val executionContext: ExecutionContext = system.dispatcher

  val userRegistryActor: ActorRef = system.actorOf(UserRegistryActor.props, "userRegistryActor")
  implicit val timeout = Timeout(5 seconds)

  //#exception-handler
  implicit val exceptionHandler = ExceptionHandler {
    case e: Exception =>
      extractUri { uri =>
        complete((StatusCodes.InternalServerError, s"Exception ${e.getMessage} happened for URI: $uri."))
      }
  }
  //#exception-handler

  //#all-routes
  lazy val routes: Route =
    //#user-post
    path("user") {
      post {
        entity(as[User]) { user =>
          userRegistryActor ! CreateUser(user)
          complete((StatusCodes.Created, s"User ${user.name} created."))
        }
      }
    } ~ //#user-post
      //#user-get-delete
      path("user" / Segment) { name =>
        get {
          //#retrieve-user-info
          val userInfo: Future[UserInfo] = (userRegistryActor ? GetUser(name)).mapTo[UserInfo]
          onComplete(userInfo) { r =>
            r match {
              case Success(UserInfo(Some(user))) => complete(user)
              case Success(UserInfo(None)) => complete((StatusCodes.OK, s"User $name is not registered."))
              case Failure(ex) => complete((StatusCodes.InternalServerError, ex))
            }
          }
          //#retrieve-user-info
        } ~
          delete {
            userRegistryActor ! DeleteUser(name)
            complete((StatusCodes.OK, s"User $name deleted."))
          }
      } ~ //#user-get-delete
      //#users-get
      path("users") {
        get {
          val users: Future[Users] = (userRegistryActor ? GetUsers).mapTo[Users]
          complete(users)
        }
      } //#users-get
  //#all-routes

  //#http-server
  val serverBindingFuture: Future[ServerBinding] = Http().bindAndHandle(routes, "localhost", 8080)
  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine()
  serverBindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
  //#http-server
  //#main-class
}
//#main-class
