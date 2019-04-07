package com.datahack.sepsissolutions.dao

import akka.actor.Actor
import com.datahack.sepsissolutions.SepsisUtils
import com.datahack.sepsissolutions.dao.DaoActor.GetZombiePatients
import com.datahack.sepsissolutions.model.Prueba._
import com.datahack.sepsissolutions.model.{PatientScore, Prueba}
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContextExecutor

object DaoActor{
  case class InsertPruebasIfNotExists(pruebas: Seq[Prueba])
  case object GetPatientsWithNewPruebas
  case class GetPruebasForPatient(idPaciente: IdPaciente)
  case class SetScoreForPatient(idPaciente: IdPaciente, score: Double)
  case object GetZombiePatients



  case class PatientsWithNewPruebas(patients: Seq[IdPaciente])

  case class PatientPruebas(idPaciente: IdPaciente, pruebas: Seq[Prueba])

  case class ZombiePatients(patients: Seq[PatientScore])

  case object DeleteOldElements

  case object FinishedInsertingPruebas
}


class DaoActor(pruebaDao: PruebaDao, patientsDao: PatientsDao) extends Actor {

  import SepsisUtils._
  import DaoActor._

  override def receive: Receive = {
    case InsertPruebasIfNotExists(pruebas) => {
      sepsislog.info("Inserting Pruebas")
      pruebas.foreach(prueba => {
        if (!pruebaDao.existsPrueba(prueba)) {
          pruebaDao.insertPrueba(prueba)
        }
        else{
          sepsislog.debug("Prueba exists")
        }
        }
      )
      sepsislog.info("Notifying scoring actor I am finished inserting pruebas")
      sender ! FinishedInsertingPruebas
    }

    case GetPatientsWithNewPruebas => {
      sender ! PatientsWithNewPruebas(pruebaDao.getPatientsWithNewPruebas())
    }

    case GetPruebasForPatient(idPaciente: IdPaciente) => {
      sender ! PatientPruebas(idPaciente,pruebaDao.getPatientPruebas(idPaciente))
    }

    case SetScoreForPatient(idPaciente: IdPaciente,score:Double) => {
      sepsislog.info(s"Setting Score $score for patient $idPaciente")
      patientsDao.setPatientScore(idPaciente, score)
    }

    case GetZombiePatients => {
      sender ! ZombiePatients(patientsDao.getPatientsWithScoreGreaterThan(0.5))
    }

    case DeleteOldElements => {
      pruebaDao.deleteOldPruebas()
      patientsDao.deleteOldPatients()
    }

    case _ => {
      println("Lalala")
    }
  }
}
