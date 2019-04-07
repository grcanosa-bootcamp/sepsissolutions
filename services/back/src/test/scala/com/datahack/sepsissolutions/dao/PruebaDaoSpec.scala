package com.datahack.sepsissolutions.dao


import java.time.LocalDateTime
import java.time.temporal.{ChronoUnit, TemporalUnit}

import com.datahack.sepsissolutions.model.Prueba
import com.datahack.sepsissolutions.model.Prueba.IdPaciente
import com.datahack.sepsissolutions.testutils.CassandraTestUtils
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

import scala.collection.immutable

class PruebaDaoSpec
  extends WordSpec
    with Matchers
    with BeforeAndAfterAll
    with CassandraTestUtils {

  lazy val session = createTestConnection


  def fixture = new {
    implicit val keySpaceName = createTestKeyspace(session).get
    createTablePruebas(session,keySpaceName)
    val pruebaDao: PruebaDao = new PruebaDao(session)

  }

  "An empty PruebaDao" should {

    val f = fixture
    val prueba1 = Prueba("1",LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),"prueba1",1.0,"%")

    "return false when a prueba is not inserted" in {
      val exists = f.pruebaDao.existsPrueba(prueba1)
      exists shouldBe false
    }

    "insert a prueba when requested" in {
      f.pruebaDao.insertPrueba(prueba1)
      val seq = getPruebasByIdPaciente(session,f.keySpaceName,prueba1.idPaciente)
      seq.length shouldBe 1
      seq.head shouldBe prueba1
    }


    "return true if a prueba is already inserted" in {
      val exists = f.pruebaDao.existsPrueba(prueba1)
      exists shouldBe true
    }
  }

  "A populated prueba dao" should {
    val f = fixture
    val patients = (1 to 5).map(_.toString)
    val pruebas: Seq[Prueba] = patients.map(Prueba(_,LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),"prueba1",1,"%"))

    val patient6:IdPaciente = "patient6"
    val pruebaspatient6 = (1 to 5).map(n => Prueba("patient6"
                          ,LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
                          ,"prueba"+n.toString,n,"%"))

    pruebas.foreach(f.pruebaDao.insertPrueba)
    pruebaspatient6.foreach(f.pruebaDao.insertPrueba)

    "get all patients as having new pruebas" in {
      val patientsGet = f.pruebaDao.getPatientsWithNewPruebas()

      patientsGet should contain allElementsOf patients
    }

    "get pruebas for a single patient" in {
      val pruebasGet = f.pruebaDao.getPatientPruebas(patient6)
      pruebasGet should contain allElementsOf pruebaspatient6
    }
  }


  override protected def afterAll() = {
   dropAllKeyspaces(session)
  }


}
