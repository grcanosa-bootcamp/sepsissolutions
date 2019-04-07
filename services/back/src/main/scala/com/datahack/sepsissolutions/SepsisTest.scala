package com.datahack.sepsissolutions

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest}

object SepsisTest extends App {

  import SepsisUtils._

  val req = HttpRequest(
    method = HttpMethods.POST,
    uri = "https://rzu7o7u292.execute-api.eu-west-1.amazonaws.com/predict",
    entity = HttpEntity(ContentTypes.`application/json`, """{"test":"laalla"}""")
  )
  val resp = Http().singleRequest(req)
  resp.map(s => println(s.toString()))
  Thread.sleep(10)

}
