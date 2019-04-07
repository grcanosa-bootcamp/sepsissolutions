package com.datahack.sepsissolutions.dao

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

import com.datahack.sepsissolutions.model.Prueba
import com.datahack.sepsissolutions.model.Prueba.IdPaciente
import com.datahack.sepsissolutions.testutils.CassandraTestUtils
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

class PatientDaoSpec
  extends WordSpec
      with Matchers
      with BeforeAndAfterAll
      with CassandraTestUtils {

    lazy val session = createTestConnection


    def fixture = new {
      implicit val keySpaceName = createTestKeyspace(session).get
      createTablePatients(session,keySpaceName)
      val patientDao: PatientsDao= new PatientsDao(session)

    }

    "An empty PatientsDao" should {


      "set the score for a patient" in {

      }

      "update the score for the same patient" in {

      }

      "get an empty list if no patient has score greater than 0.5" in {


      }

      "return all patients if max score is 0 " in {

      }

      "delete old patients" in {

      }
    }

    override protected def afterAll() = {
     dropAllKeyspaces(session)
    }


  }
