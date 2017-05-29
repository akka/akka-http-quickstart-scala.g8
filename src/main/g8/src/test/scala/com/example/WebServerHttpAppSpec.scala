package com.example

import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport.defaultNodeSeqUnmarshaller
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{ Matchers, WordSpec }

import scala.xml.NodeSeq

class WebServerHttpAppSpec extends WordSpec with Matchers with ScalatestRouteTest {

  "WebServiceHttpApp" should {
    "answer to any request to `/`" in {
      Get("/") ~> WebServerHttpApp.routes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[String] shouldBe "Server up and running"
      }
      Post("/") ~> WebServerHttpApp.routes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[String] shouldBe "Server up and running"
      }
    }
    "answer to GET requests to `/hello`" in {
      Get("/hello") ~> WebServerHttpApp.routes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[NodeSeq] shouldBe <html><body><h1>Say hello to akka-http</h1></body></html>
      }
    }
    "not handle a POST request to `/hello`" in {
      Post("/hello") ~> WebServerHttpApp.routes ~> check {
        handled shouldBe false
      }
    }
    "respond with 405 when not issuing a GET to `/hello` and route is sealed" in {
      Put("/hello") ~> Route.seal(WebServerHttpApp.routes) ~> check {
        status shouldBe StatusCodes.MethodNotAllowed
      }
    }
  }

}
