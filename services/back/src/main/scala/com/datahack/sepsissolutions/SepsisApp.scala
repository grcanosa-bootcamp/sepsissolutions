package com.datahack.sepsissolutions

import scala.concurrent.duration._

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import com.datahack.sepsissolutions.dao.{DaoActor, DaoManager, PatientsDao, PruebaDao}
import com.datahack.sepsissolutions.fileprocess.{FileProcessActor, FileWatcher}
import com.datahack.sepsissolutions.dao.{DaoActor, DaoManager, PatientsDao, PruebaDao}
import com.datahack.sepsissolutions.fileprocess.FileWatcher
import com.datahack.sepsissolutions.restapi.PatientController
import com.datahack.sepsissolutions.score.ScoringActor
import com.datastax.driver.core.Session

import scala.io.StdIn
import scala.util.Try



object SepsisApp extends App  {

  import SepsisUtils._

  sepsislog.info("STARTING SEPSIS APP, waiting 2 seconds for DB to be ready")
  val timeout = config.getInt("cassandra.connection_timeout").seconds
  sepsislog.info(s"Timeout to cassandra connection $timeout")
  val session_option = DaoManager.blockUntilConnect(timeout)

  if(session_option.isEmpty){
    sepsislog.info("Couldn't connect, exiting")
    System.exit(1)
  }

  implicit lazy val session: Session = session_option.get

  DaoManager.init()
  import DaoManager._

  val pruebaDao = new PruebaDao(session)
  val patientsDao = new PatientsDao(session)

  //!ACTORS
  lazy val daoActor: ActorRef = system.actorOf(Props(classOf[DaoActor], pruebaDao,patientsDao), "DaoActor")
  lazy val scoringActor: ActorRef = system.actorOf(Props(classOf[ScoringActor],daoActor), "ScoringActor")
  lazy val fileProcessActor = system.actorOf(Props(classOf[FileProcessActor],daoActor,scoringActor),"FileProcessActor")

  //!RESTAPI CONTROLLER

  lazy val patientController = new PatientController(daoActor)


  //START HTTP SERVER
  val host = Try(config.getString("http.host")).getOrElse("0.0.0.0")
  val port = Try(config.getInt("http.port")).getOrElse(80)
  val httpServer = Http().bindAndHandle(patientController.routes,host,port)

  httpServer.map(
    server =>
      sepsislog.info(s"server listening on ${server.localAddress.getHostName}:${server.localAddress.getPort}")
  )

  //FILE WATCHER
  val defMonDir = "/data"
  val monitorDir = Try{config.getString("sepsis.datadir")}.getOrElse(defMonDir)
  lazy val fileWatcher = new FileWatcher(fileProcessActor,monitorDir)

  fileWatcher.startWatcher()





  sys.addShutdownHook(httpServer.flatMap(_.unbind()))
  sys.addShutdownHook(session.close())
  sys.addShutdownHook(system.terminate())

  StdIn.readLine()

}
