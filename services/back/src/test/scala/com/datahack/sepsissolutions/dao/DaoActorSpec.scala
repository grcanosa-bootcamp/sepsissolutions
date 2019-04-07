package com.datahack.sepsissolutions.dao

import scala.concurrent.duration._
import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{TestActorRef, TestKit, TestProbe}
import com.datahack.sepsissolutions.dao.DaoActor.{FinishedInsertingPruebas, InsertPruebasIfNotExists}
import com.datahack.sepsissolutions.testutils.CassandraTestUtils
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class DaoActorSpec
  extends TestKit(ActorSystem("DaoActorSpec"))
    with WordSpecLike
  with BeforeAndAfterAll
    with Matchers
  with CassandraTestUtils{

  val session = createTestConnection
  implicit val keySpace = createTestKeyspace(session).get

  createTablePruebas(session,keySpace)
  createTablePatients(session,keySpace)

  val pruebaDao = new PruebaDao(session)
  val patientDao = new PatientsDao(session)


  val daoActor = TestActorRef[DaoActor](new DaoActor(pruebaDao,patientDao))

  "DaoActor" should {
    "return FinishedInsertingPruebas after asking to insert pruebas" in {
      val sender = TestProbe()
      implicit val senderRef: ActorRef = sender.ref
      val pruebas = getTestPruebas()

      daoActor ! InsertPruebasIfNotExists(pruebas)

      sender.expectMsgType[FinishedInsertingPruebas.type](5 seconds)

    }

    "not insert repeated pruebas" in {
      ???
    }

    "return patients with new pruebas" in {
      ???
    }

    "return pruebas for patient" in {
      ???
    }

    "set score for patient" in {
      ???
    }

    "get zombie patients" in {
      ???
    }

    "delete old elements" in {
      ???
    }





  }

  override protected def afterAll() = {
    dropAllKeyspaces(session)
  }

}
