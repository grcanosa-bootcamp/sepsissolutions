package com.datahack.sepsissolutions

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContextExecutor

object SepsisUtils {

  val sepsislog  = LoggerFactory.getLogger("sepsissolutions")

  lazy val config = {
    sepsislog.info("Cargando configuraciones de myapplication.conf")
    ConfigFactory.load("myapplication.conf") // o .load()
  }

  implicit lazy val system: ActorSystem = ActorSystem("akka-sepsis")  // ActorMaterializer requires an implicit ActorSystem
  implicit lazy val ec: ExecutionContextExecutor = system.dispatcher // bindingFuture.map requires an implicit ExecutionContext
  implicit lazy val materializer: ActorMaterializer = ActorMaterializer()

}
