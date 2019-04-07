package com.datahack.sepsissolutions.model

import java.time.{Instant, LocalDateTime, ZoneOffset}


import com.datastax.driver.core.Row
import com.datahack.sepsissolutions.dao.DaoManager.PRUEBA_TABLE


import Prueba._

case class Prueba(idPaciente: IdPaciente
                   , dateTime: LocalDateTime
                   , name: String
                   , value: Double
                   , unit: String)

case class Pruebas(pruebas: Seq[Prueba])

object Prueba{
  type IdPaciente = String

  def fromRow(r:Row) = {
    Prueba(idPaciente = r.getString(PRUEBA_TABLE.IDPACIENTE_COLUMN)
      , dateTime = LocalDateTime.ofEpochSecond(
        r.getLong(PRUEBA_TABLE.FECHAPRUEBA_COLUMN),0,
        ZoneOffset.UTC)
      , name = r.getString(PRUEBA_TABLE.NAMEPRUEBA_COLUMN)
      , value = r.getDouble(PRUEBA_TABLE.VALUEPRUEBA_COLUMN)
      , unit = r.getString(PRUEBA_TABLE.UNITPRUEBA_COLUMN))
  }
}


