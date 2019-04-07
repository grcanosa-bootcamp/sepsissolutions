package com.datahack.sepsissolutions.testutils


import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

import com.datahack.sepsissolutions.dao.DaoManager.{PATIENTS_TABLE, PRUEBA_TABLE, keySpaceName}
import com.datahack.sepsissolutions.model.Prueba
import com.datahack.sepsissolutions.model.Prueba.IdPaciente
import com.datastax.driver.core.schemabuilder.SchemaBuilder
import com.datastax.driver.core.{Cluster, DataType, Session, SocketOptions}
import com.typesafe.config.ConfigFactory

import scala.util.Try

trait CassandraTestUtils {

  var keySpaces = scala.collection.mutable.Seq.empty[String]

  lazy val r = scala.util.Random

  lazy val config = {
    ConfigFactory.load("myapplication.conf") // o .load()
  }

  lazy val cassandrahost = Try{config.getString("cassandra.host")}.getOrElse("localhost")


  def createTestConnection: Session = {
    val cluster: Try[Cluster] = Try {
      Cluster.builder()
        .addContactPoint(cassandrahost)
        //.withCredentials("cassandra", "cassandra")
        .withSocketOptions(
          new SocketOptions()
            .setReadTimeoutMillis(30000)).build()
    }

    cluster.get.connect()
  }

  def createTestKeyspace(session:Session): Try[String] = Try{
    val keySpaceName = s"key${System.currentTimeMillis()}_${r.nextInt(1000)}"
    keySpaces  = keySpaces :+ keySpaceName
    session.execute(
      s"""
         |CREATE KEYSPACE $keySpaceName
         |WITH replication = {'class':'SimpleStrategy', 'replication_factor':1}
         |""".stripMargin
    )
    keySpaceName
  }

  def dropAllKeyspaces(session:Session) = {
    keySpaces.foreach(dropKeySpace(session,_))
  }

  def createTablePruebas(session:Session,keySpace:String): Try[Boolean] = {
    val create = SchemaBuilder
      .createTable(keySpace, PRUEBA_TABLE.name)
      .ifNotExists()
      .addPartitionKey(PRUEBA_TABLE.IDPACIENTE_COLUMN, DataType.varchar())
      .addClusteringColumn(PRUEBA_TABLE.FECHAPRUEBA_COLUMN, DataType.bigint())
      .addClusteringColumn(PRUEBA_TABLE.NAMEPRUEBA_COLUMN, DataType.varchar())
      .addColumn(PRUEBA_TABLE.VALUEPRUEBA_COLUMN, DataType.cdouble())
      .addColumn(PRUEBA_TABLE.UNITPRUEBA_COLUMN, DataType.varchar())
      .addColumn(PRUEBA_TABLE.FECHAINSERT_COLUMN, DataType.bigint())

    Try {
      session.execute(create)
    }.map(_.isExhausted)

  }

  def createTablePatients(session:Session,keyspace:String) = {
    val create = SchemaBuilder
      .createTable(keySpaceName, PATIENTS_TABLE.name)
      .ifNotExists()
      .addPartitionKey(PATIENTS_TABLE.IDPACIENTE_COLUMN,DataType.varchar())
      .addColumn(PATIENTS_TABLE.SCORE_COLUMN,DataType.cdouble())
      .addColumn(PATIENTS_TABLE.FECHAINSERT_COLUMN,DataType.bigint())


    Try{
      session.execute(create)
    }.map(_.isExhausted)
  }

  def getPruebasByIdPaciente(session:Session, keySpace:String,idPaciente:IdPaciente) = {
    import scala.collection.JavaConverters._
    val result = session.execute(
      s"""
         |SELECT * FROM $keySpace.${PRUEBA_TABLE.name} WHERE ${PRUEBA_TABLE.IDPACIENTE_COLUMN} = '$idPaciente'
       """.stripMargin
    )
    result.iterator().asScala.toSeq.map(Prueba.fromRow)
  }

  def dropKeySpace(session: Session, keySpaceName: String): Unit = {
    session.execute(s"DROP KEYSPACE IF EXISTS $keySpaceName")
  }


  def getTestPruebas()= (1 to 5).map(n => Prueba(n.toString, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), "prueba1", 1, "%"))
}
