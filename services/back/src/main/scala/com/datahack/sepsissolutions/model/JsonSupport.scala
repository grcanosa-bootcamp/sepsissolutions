package com.datahack.sepsissolutions.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, PrettyPrinter, RootJsonFormat}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol{

  implicit val printer = PrettyPrinter

  implicit val localDateTimeFormat = new JsonFormat[LocalDateTime] {
    private val iso_date_time = DateTimeFormatter.ISO_DATE_TIME
    def write(x: LocalDateTime) = JsString(iso_date_time.format(x))
    def read(value: JsValue) = value match {
      case JsString(x) => LocalDateTime.parse(x, iso_date_time)
      case x => throw new RuntimeException(s"Unexpected type ${x.getClass.getName} when trying to parse LocalDateTime")
    }
  }

  implicit val pruebaFormat = jsonFormat5(Prueba.apply)
  implicit val pruebasFormat = jsonFormat1(Pruebas)

  implicit val scoreFormat = jsonFormat1(Score)

  implicit val patientScore = jsonFormat2(PatientScore)
}
