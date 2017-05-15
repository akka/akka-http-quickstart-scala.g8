package com.example.routes

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.PathDirectives.pathEndOrSingleSlash
import akka.http.scaladsl.server.directives.RouteDirectives.complete

/**
 * Routes can be defined in separated classes like shown in here
 */
object BaseRoutes {

  // This route is the one that listens to the top level '/'
  lazy val baseRoutes: Route =
    pathEndOrSingleSlash { // Listens to the top `/`
      complete("Server up and running") // Completes with some text
    }
}
