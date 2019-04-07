package com.datahack.sepsissolutions.dao

import java.time.{LocalDateTime, ZoneOffset}
import java.util.{Date, TimeZone}

import com.datahack.sepsissolutions.model.Prueba
import com.datahack.sepsissolutions.model.Prueba._
import com.datastax.driver.core.{ Row, Session}
import com.datastax.driver.core.querybuilder.QueryBuilder

import scala.util.Try
import DaoManager._
import com.datahack.sepsissolutions.SepsisUtils

class PruebaDao(session:Session)(implicit val keySpaceName: String){


  def getCurrentTimestamp(): Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)


  def existsPrueba(prueba: Prueba): Boolean = {
    val selectQuery = QueryBuilder
      .select(PRUEBA_TABLE.IDPACIENTE_COLUMN, PRUEBA_TABLE.FECHAPRUEBA_COLUMN, PRUEBA_TABLE.NAMEPRUEBA_COLUMN)
      .from(keySpaceName, PRUEBA_TABLE.name)
      .where(QueryBuilder.eq(PRUEBA_TABLE.IDPACIENTE_COLUMN, prueba.idPaciente))
      .and(QueryBuilder.eq(PRUEBA_TABLE.FECHAPRUEBA_COLUMN, prueba.dateTime.toEpochSecond(ZoneOffset.UTC)))
      .and(QueryBuilder.eq(PRUEBA_TABLE.NAMEPRUEBA_COLUMN, prueba.name))

    Try{
      session.execute(selectQuery)
    }.toOption match {
      case None => false
      case Some(r) => r.one() match {
        case null => false
        case r: Row => true
      }
    }
  }


  def insertPrueba(prueba: Prueba): Try[Boolean] = {
    import scala.collection.JavaConverters._

    val query = QueryBuilder
      .insertInto(keySpaceName, PRUEBA_TABLE.name)
      .value(PRUEBA_TABLE.IDPACIENTE_COLUMN, prueba.idPaciente)
      .value(PRUEBA_TABLE.FECHAPRUEBA_COLUMN, prueba.dateTime.toEpochSecond(ZoneOffset.UTC))
      .value(PRUEBA_TABLE.NAMEPRUEBA_COLUMN, prueba.name)
      .value(PRUEBA_TABLE.VALUEPRUEBA_COLUMN, prueba.value)
      .value(PRUEBA_TABLE.UNITPRUEBA_COLUMN, prueba.unit)
      .value(PRUEBA_TABLE.FECHAINSERT_COLUMN, getCurrentTimestamp())

    Try {
      session.execute(query)
    }.map(_.isExhausted)
  }


  var lastPatientsWithNewPruebasCheck = 0L

  def getPatientsWithNewPruebas(): Seq[IdPaciente] = {
    import scala.collection.JavaConverters._

    val selectQuery = QueryBuilder
      .select(PRUEBA_TABLE.IDPACIENTE_COLUMN)
      .from(keySpaceName, PRUEBA_TABLE.name).allowFiltering()
      .where(QueryBuilder.gte(PRUEBA_TABLE.FECHAINSERT_COLUMN, lastPatientsWithNewPruebasCheck))


    Try {
      session.execute(selectQuery)
    }.map(rs => {
      lastPatientsWithNewPruebasCheck = getCurrentTimestamp()
      rs.asScala.toSeq
        .map(r => r.getString(PRUEBA_TABLE.IDPACIENTE_COLUMN)).distinct
    }).getOrElse(Seq.empty[IdPaciente])

//    Seq.empty[IdPaciente]
  }

  def getPatientPruebas(idPaciente: IdPaciente) = {
    import scala.collection.JavaConverters._

    val selectQuery = QueryBuilder
      .select().all()
      .from(keySpaceName,PRUEBA_TABLE.name)
      .where(QueryBuilder.eq(PRUEBA_TABLE.IDPACIENTE_COLUMN,idPaciente))

     session.execute(selectQuery).asScala.toSeq.map(Prueba.fromRow)
  }


  def deleteOldPruebas() = {

    val max_timestamp = getCurrentTimestamp()-48*3600

    val deleteQuery1 = QueryBuilder.delete().all()
          .from(keySpaceName,PRUEBA_TABLE.name)
      .where(QueryBuilder.lt(PRUEBA_TABLE.FECHAINSERT_COLUMN,max_timestamp))

    Try{
      session.execute(deleteQuery1)
    }.map(_.isExhausted)

  }




}
