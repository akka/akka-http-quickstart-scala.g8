package com.example

import org.scalatest.{ Matchers, WordSpec }
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server._
import Directives._


class WebServerSpec extends WordSpec with Matchers with ScalatestRouteTest {

  "WebService" should {
    "answer to GET requests to `/hello`" in {
      Get("/hello") ~> WebServer.route ~> check {
        status shouldBe StatusCodes.OK
        responseAs[String] shouldBe "<h1>Say hello to akka-http</h1>"
      }
    }
    "not handle a POST request to `/hello`" in {
      Post("/hello") ~> WebServer.route ~> check {
        handled shouldBe false
      }
    }
    "respond with 405 when not issuing a GET to `/hello` and route is sealed" in {
      Put("/hello") ~> Route.seal(WebServer.route) ~> check {
        status shouldBe StatusCodes.MethodNotAllowed
      }
    }
  }

}
