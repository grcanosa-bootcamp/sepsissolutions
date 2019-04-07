package com.datahack.sepsissolutions.dao

import java.time.{LocalDateTime, ZoneOffset}

import com.datahack.sepsissolutions.dao.DaoManager._
import com.datahack.sepsissolutions.model.PatientScore
import com.datahack.sepsissolutions.model.Prueba._
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.{Row, Session}

import scala.util.Try

class PatientsDao(session:Session)(implicit val keySpaceName: String) {


  def getCurrentTimestamp(): Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)


  def setPatientScore(idPaciente: IdPaciente, score: Double) = {

    import scala.collection.JavaConverters._
    val insertQuery = QueryBuilder
      .insertInto(keySpaceName,PATIENTS_TABLE.name)
      .value(PATIENTS_TABLE.IDPACIENTE_COLUMN,idPaciente)
      .value(PATIENTS_TABLE.SCORE_COLUMN, score)
      .value(PATIENTS_TABLE.FECHAINSERT_COLUMN, getCurrentTimestamp())

//    Try {
      session.execute(insertQuery)
  //  }.map(_.isExhausted)
  }

  def getPatientsWithScoreGreaterThan(score: Double) = {
    import scala.collection.JavaConverters._
    val selectQuery = QueryBuilder
      .select()
      .all()
      .from(keySpaceName,PATIENTS_TABLE.name).allowFiltering()
      .where(QueryBuilder.gte(PATIENTS_TABLE.SCORE_COLUMN,score))

    session.execute(selectQuery).asScala.toSeq.map(r =>
                  PatientScore(r.getString(PATIENTS_TABLE.IDPACIENTE_COLUMN),
                    r.getDouble(PATIENTS_TABLE.SCORE_COLUMN))).sortBy(ps => ps.score)(Ordering[Double].reverse)
  }


  def deleteOldPatients() = {
    val max_timestamp = getCurrentTimestamp()-48*3600

    val deleteQuery1 = QueryBuilder.delete().all()
      .from(keySpaceName,PATIENTS_TABLE.name)
      .where(QueryBuilder.lt(PATIENTS_TABLE.FECHAINSERT_COLUMN,max_timestamp))


    Try{
      session.execute(deleteQuery1)
    }.map(_.isExhausted)
  }



}
