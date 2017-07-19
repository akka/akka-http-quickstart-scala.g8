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

  // Required by the `ask` (?) method below
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
    //#users-get-post
    //#users-get-delete
    pathPrefix("users") {
      //#users-get-delete
      pathEnd {
        get {
          val users: Future[Users] = (userRegistryActor ? GetUsers).mapTo[Users]
          complete(users)
        } ~
          post {
            entity(as[User]) { user =>
              val userCreated: Future[ActionPerformed] = (userRegistryActor ? CreateUser(user)).mapTo[ActionPerformed]
              onComplete(userCreated) { r =>
                r match {
                  case Success(ActionPerformed(description)) => complete((StatusCodes.Created, description))
                  case Failure(ex) => complete((StatusCodes.InternalServerError, ex))
                }
              }
            }
          }
      } ~
        //#users-get-post
        //#users-get-delete
        path(Segment) { name =>
          get {
            //#retrieve-user-info
            val maybeUser: Future[Option[User]] = (userRegistryActor ? GetUser(name)).mapTo[Option[User]]
            rejectEmptyResponse {
              complete(maybeUser)
            }
            //#retrieve-user-info
          } ~
            delete {
              val userDeleted: Future[ActionPerformed] = (userRegistryActor ? DeleteUser(name)).mapTo[ActionPerformed]
              onComplete(userDeleted) { r =>
                r match {
                  case Success(ActionPerformed(description)) => complete((StatusCodes.OK, description))
                  case Failure(ex) => complete((StatusCodes.InternalServerError, ex))
                }
              }
            }
        }
      //#users-get-delete
    }
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
