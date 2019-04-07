package com.datahack.sepsissolutions.score

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.marshalling.Marshal
import com.datahack.sepsissolutions.model.{JsonSupport, Prueba, Pruebas, Score}

import scala.concurrent.{ExecutionContext, Future}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.stream.ActorMaterializer
import com.datahack.sepsissolutions.SepsisUtils

object ScoringServiceCaller extends JsonSupport{

  import SepsisUtils._

  def responseToScore(response: HttpResponse) = {
    try{
      Unmarshal(response).to[Score]
    }catch{
      case e:Exception  => sepsislog.info(e.toString)
        Future{Score(0)}
    }
  }

  def request(pruebas: Seq[Prueba]) = {

   Marshal(Pruebas(pruebas)).to[RequestEntity].flatMap{e =>
     Http().singleRequest(
      HttpRequest(
        method = HttpMethods.POST,
        uri = "https://rzu7o7u292.execute-api.eu-west-1.amazonaws.com/predict",
        entity = e
      )
     )
    }



  }


  def getScoreForPatient(pruebas: Seq[Prueba]): Future[Score] = {

    val response: Future[HttpResponse] = request(pruebas)

    response.flatMap(responseToScore)
  }
}
