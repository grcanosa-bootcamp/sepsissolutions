package com.datahack.sepsissolutions.restapi
import scala.concurrent.duration._
import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server
import akka.http.scaladsl.server.{Directives, RequestContext}
import akka.util.Timeout
import akka.pattern.ask
import com.datahack.sepsissolutions.dao.DaoActor.{GetPruebasForPatient, GetZombiePatients, PatientPruebas, ZombiePatients}
import com.datahack.sepsissolutions.model.JsonSupport

import scala.concurrent.ExecutionContext


class PatientController(daoActor: ActorRef)(implicit executionContext:ExecutionContext)
  extends Directives
  with JsonSupport   {


  val routes = pathPrefix("api") { getZombiePatients ~ getPatientPruebas }

  implicit val timeout: Timeout = Timeout(60 seconds)

  def getZombiePatients : server.Route =
    path("zombiepatients") {
      get {
        onSuccess(daoActor ? GetZombiePatients) {
          case ZombiePatients(list) => complete(list)
          case _ => complete(StatusCodes.InternalServerError)
        }
      }
    }

  def getPatientPruebas =
    path("patientpruebas" / Segment ) { patientid =>
      get {
        onSuccess(daoActor ? GetPruebasForPatient(patientid)) {
          case PatientPruebas(idPaciente,pruebas) => complete(pruebas)
          case _ => complete(StatusCodes.InternalServerError)
        }
      }
    }

}
