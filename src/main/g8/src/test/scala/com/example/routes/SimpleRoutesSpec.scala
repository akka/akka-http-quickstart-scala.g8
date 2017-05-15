package com.example.routes

import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport.defaultNodeSeqUnmarshaller
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{ Matchers, WordSpec }

import scala.xml.NodeSeq

class SimpleRoutesSpec extends WordSpec with Matchers with ScalatestRouteTest with SimpleRoutes {

  "SimpleRoute" should {
    "answer to GET requests to `/hello`" in {
      Get("/hello") ~> simpleRoutes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[NodeSeq] shouldBe <html><body><h1>Say hello to akka-http</h1></body></html>
      }
    }
    "not handle a POST request to `/hello`" in {
      Post("/hello") ~> simpleRoutes ~> check {
        handled shouldBe false
      }
    }
    "respond with 405 when not issuing a GET to `/hello` and route is sealed" in {
      Put("/hello") ~> Route.seal(simpleRoutes) ~> check {
        status shouldBe StatusCodes.MethodNotAllowed
      }
    }
  }

}
