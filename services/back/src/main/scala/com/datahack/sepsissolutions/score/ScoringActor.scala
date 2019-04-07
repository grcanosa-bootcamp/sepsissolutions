package com.datahack.sepsissolutions.score

import akka.actor.{Actor, ActorRef}
import com.datahack.sepsissolutions.SepsisUtils
import com.datahack.sepsissolutions.model.Prueba
import com.datahack.sepsissolutions.dao.DaoActor._
import com.datahack.sepsissolutions.model.Prueba._
import scala.concurrent.duration._
import scala.util.{Failure, Success}

class ScoringActor(daoActor : ActorRef) extends Actor {

  import SepsisUtils._

  var numPatients = 0


  var badPatientRequests = scala.collection.mutable.Map.empty[IdPaciente,Int]

  override def receive: Receive = {

    case FinishedInsertingPruebas => sender ! GetPatientsWithNewPruebas

    case PatientsWithNewPruebas(listPatients) => {
      sepsislog.info("Beginning scoring process")
      numPatients = numPatients + listPatients.size
      listPatients.foreach(sender ! GetPruebasForPatient(_))
    }

    case PatientPruebas(idPaciente: IdPaciente, pruebas: Seq[Prueba]) => {
      sepsislog.info(s"Scoring $idPaciente, numpruebas: ${pruebas.size}")
      ScoringServiceCaller.getScoreForPatient(pruebas)
        .onComplete {

          case Success(score) =>
            sepsislog.info(s"Score for patient $idPaciente is ${score.probability}")
            daoActor ! SetScoreForPatient(idPaciente, score.probability)
            numPatients = numPatients - 1
            if(numPatients == 0) {
              daoActor ! DeleteOldElements
            }

          case Failure(e)   =>

            // TODO reintentos
            if(badPatientRequests.contains(idPaciente)){
              if(badPatientRequests(idPaciente) == 3){
                sepsislog.info(s"Could not retrieve score for $idPaciente")
                badPatientRequests.remove(idPaciente)
                numPatients -= 1
              }else{
                badPatientRequests(idPaciente) += 1
                context.system.scheduler.scheduleOnce(300 milliseconds, self , PatientPruebas(idPaciente,pruebas))
              }
            }else{
              badPatientRequests(idPaciente) = 1
            }


            if(numPatients == 0) {
              daoActor ! DeleteOldElements
            }
        }


    }
  }
}
