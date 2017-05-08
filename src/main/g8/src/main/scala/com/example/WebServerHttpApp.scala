package com.example

import akka.http.scaladsl.model.{ ContentTypes, HttpEntity }
import akka.http.scaladsl.server.{ HttpApp, Route }

/**
 * Server will be started calling `WebServerHttpApp.startServer("localhost", 8080)`
 * and it will be shutdown after pressing return.
 */
object WebServerHttpApp extends HttpApp with App {
  // Routes that this WebServer must handle are defined here
  def route: Route = pathEndOrSingleSlash { // Listens to the top `/`
    complete("Server up and running") // Completes with some text
  } ~
    path("hello") { // Listens to paths that are exactly `/hello`
      get { // Listens only to GET requests
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>")) // Completes with some text
      }
    }

  // This will start the server until the return key is pressed
  startServer("localhost", 8080)
}
