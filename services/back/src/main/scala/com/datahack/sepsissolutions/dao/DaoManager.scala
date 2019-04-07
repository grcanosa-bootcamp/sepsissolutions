package com.datahack.sepsissolutions.dao
import scala.concurrent.duration._
import com.datahack.sepsissolutions.SepsisUtils
import com.datastax.driver.core.{Cluster, DataType, Session}
import com.datastax.driver.core.schemabuilder.SchemaBuilder

import scala.util.Try

object DaoManager {

  import SepsisUtils._

  lazy val cassandrahost = Try{config.getString("cassandra.host")}.getOrElse("localhost")

  //lazy val cluster: Try[Cluster] = Try{Cluster.builder().addContactPoint(cassandrahost).build()}


  def blockUntilConnect(timeout: FiniteDuration): Option[Session] = {
    sepsislog.info(s"CONNECTING TO CASSANDRA AT: $cassandrahost")
    if(timeout.toNanos < 0){
      None
    }else{
      Try{Cluster.builder()
        .addContactPoint(cassandrahost)
        .build()
        .connect()}.toOption match {
        case Some(s) => Some(s)
        case None => {
          Thread.sleep(5000)
          blockUntilConnect(timeout - (5 seconds))
        }
      }
    }
  }



  implicit val keySpaceName : String = "sepsissolutions"

  val PRUEBA_TABLE = new {
    val name: String = "pruebas"
    val IDPACIENTE_COLUMN: String = "id_paciente"
    val FECHAPRUEBA_COLUMN: String = "fecha"
    val NAMEPRUEBA_COLUMN: String = "nombre"
    val VALUEPRUEBA_COLUMN: String = "valor"
    val UNITPRUEBA_COLUMN: String = "unidad"
    val FECHAINSERT_COLUMN: String = "fecha_insert"
  }

  val PATIENTS_TABLE = new {
    val name: String = "patients"
    val IDPACIENTE_COLUMN: String = "id_paciente"
    val SCORE_COLUMN: String = "score"
    val FECHAINSERT_COLUMN: String = "fecha_insert"
  }


  def init()(implicit session: Session) = {
    createKeyspace()
    createPatientsTable()
    createPruebaTable()
  }

  def createKeyspace()(implicit session: Session) = {
    Try {
      session.execute(
        s"""
           |CREATE KEYSPACE $keySpaceName
           |WITH replication = {'class':'SimpleStrategy', 'replication_factor':1}
           |""".stripMargin
      )
    }
  }

  //    val create: CreateKeyspace = SchemaBuilder
  //              .createKeyspace(keySpaceName).ifNotExists()
  //          .`with`()
  //            .replication(ImmutableMap.of(
  //                  {"class", "SimpleStrategy"},
  //                    {"replication_factor", 3}
  //                   ))
  //
  //    Try{
  //      session.execute(create)
  //    }.map()


  def createPruebaTable()(implicit session: Session) = {
    val create = SchemaBuilder
      .createTable(keySpaceName,PRUEBA_TABLE.name)
      .ifNotExists()
      .addPartitionKey(PRUEBA_TABLE.IDPACIENTE_COLUMN,DataType.varchar())
      .addClusteringColumn(PRUEBA_TABLE.FECHAPRUEBA_COLUMN,DataType.bigint())
      .addClusteringColumn(PRUEBA_TABLE.NAMEPRUEBA_COLUMN,DataType.varchar())
      .addColumn(PRUEBA_TABLE.VALUEPRUEBA_COLUMN,DataType.cdouble())
      .addColumn(PRUEBA_TABLE.UNITPRUEBA_COLUMN,DataType.varchar())
      .addColumn(PRUEBA_TABLE.FECHAINSERT_COLUMN,DataType.bigint())

    Try{
      session.execute(create)
    }.map(_.isExhausted)
  }

  def createPatientsTable()(implicit session: Session) = {
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

}
