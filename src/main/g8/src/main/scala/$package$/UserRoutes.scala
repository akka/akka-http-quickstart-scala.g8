package $package$

import scala.concurrent.duration._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route

import scala.concurrent.Future
import $package$.UserRegistry._
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout

//#import-json-formats
//#user-routes-class
class UserRoutes(userRegistry: ActorRef[UserRegistry.Command])(implicit val system: ActorSystem[_]) {
  //#user-routes-class
  import JsonFormats._
  //#import-json-formats

  // Required by the `ask` method below
  // in a real application we'd likely want to obtain the timeout from the app configuration
  // with something like `system.settings.config.getDuration("my-app.routes.ask-timeout")`
  private implicit val timeout = Timeout(5.seconds)

  //#all-routes
  //#users-get-post
  //#users-get-delete
  val userRoutes: Route =
    pathPrefix("users") {
      concat(
        //#users-get-delete
        pathEnd {
          concat(
            get {
              val users: Future[Users] = userRegistry.ask(GetUsers)
              complete(users)
            },
            post {
              entity(as[User]) { user =>
                val userCreated: Future[ActionPerformed] = userRegistry.ask(CreateUser(user, _))
                onSuccess(userCreated) { performed =>
                  complete((StatusCodes.Created, performed))
                }
              }
            })
        },
        //#users-get-post
        //#users-get-delete
        path(Segment) { name =>
          concat(
            get {
              //#retrieve-user-info
              val response: Future[GetUserResponse] = userRegistry.ask(GetUser(name, _))
              rejectEmptyResponse {
                onSuccess(response) { response =>
                  complete(response.maybeUser)
                }
              }
              //#retrieve-user-info
            },
            delete {
              //#users-delete-logic
              val userDeleted: Future[ActionPerformed] = userRegistry.ask(DeleteUser(name, _))
              onSuccess(userDeleted) { performed =>
                complete((StatusCodes.OK, performed))
              }
              //#users-delete-logic
            })
        })
      //#users-get-delete
    }
  //#all-routes
}
