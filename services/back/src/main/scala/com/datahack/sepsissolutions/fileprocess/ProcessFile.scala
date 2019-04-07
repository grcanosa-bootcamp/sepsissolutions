package com.datahack.sepsissolutions.fileprocess

import scala.io.Source
import com.datahack.sepsissolutions.model.Prueba
import java.io.File
import java.time.{LocalDate, LocalDateTime, LocalTime}

import scala.util.Try



object ProcessFile {

  def getListOfFilesOrderedByCreationTime(dir: String):List[(Long, String)] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles
        .filter(_.isFile)
        .map(elem => (elem.lastModified, elem.getAbsolutePath))
        .sortBy { case (a, b) => -a }
        .toList
    } else {
      List[(Long, String)]()
    }
  }


  def turnFileIntoLines(files: List[(Long, String)]): List[String] = {
    val lines: List[String] = Source.
      fromFile(files.head._2)
      .getLines
      .toList
    lines
  }


  def turnFileLinesIntoPruebas(lines: List[String]): Seq[Prueba] = {
    val data: Seq[Prueba] = lines.flatMap { line => Try {
      val cols = line.split('|').map(_.trim)
      val datePart: LocalDate = LocalDate.parse(cols(1))
      val timePart: LocalTime = LocalTime.parse(cols(2))
      val dateTimePrueba: LocalDateTime = LocalDateTime.of(datePart, timePart)

      Prueba(cols(0).toLowerCase, dateTimePrueba, cols(3).toLowerCase, cols(4).toDouble, cols(5).toLowerCase)
    }.toOption
    }
    data
  }

  def processFile(file: String) = {
    val lines = Source.
      fromFile(file)
      .getLines
      .toList
    turnFileLinesIntoPruebas(lines)
  }

  //
  //  def main(directory: String): List[Prueba] ={
  //    val files = getListOfFilesOrderedByCreationTime(directory)
  //
  //    if(files.nonEmpty){
  //      val lines = turnFileIntoLines(files)
  //
  //      val data: List[Prueba] = turnFileLinesIntoPruebas(lines)
  //      data
  //    }
  //    else{
  //      printf("Error")
  //      List[Prueba]()
  //    }
  //  }


}