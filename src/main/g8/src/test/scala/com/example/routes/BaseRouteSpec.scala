package com.example.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{ Matchers, WordSpec }

class BaseRouteSpec extends WordSpec with Matchers with ScalatestRouteTest with BaseRoute {

  "BaseRoute" should {
    "answer to any request to `/`" in {
      Get("/") ~> baseRoute ~> check {
        status shouldBe StatusCodes.OK
        responseAs[String] shouldBe "Server up and running"
      }
      Post("/") ~> baseRoute ~> check {
        status shouldBe StatusCodes.OK
        responseAs[String] shouldBe "Server up and running"
      }
    }
  }

}
