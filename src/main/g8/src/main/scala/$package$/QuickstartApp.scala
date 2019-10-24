package $package$

import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

//#main-class
object QuickstartApp {

  def main(args: Array[String]): Unit = {
    val rootBehavior = Behaviors.setup[Nothing] { context =>
      val userRegistryActor = context.spawn(UserRegistry(), "UserRegistryActor")
      val routes = new UserRoutes(userRegistryActor)(context.system)

      val httpServer = context.spawn(HttpServer("localhost", 8080, routes.userRoutes), "HttpServer")

      context.watch(userRegistryActor)
      context.watch(httpServer)

      Behaviors.empty
    }

    val system = ActorSystem[Nothing](rootBehavior, "HelloAkkaHttpServer")
  }
}
//#main-class
