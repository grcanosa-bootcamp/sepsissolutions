package com.datahack.sepsissolutions.fileprocess

import akka.actor.{Actor, ActorRef}
import better.files._
import com.datahack.sepsissolutions.SepsisUtils
import com.datahack.sepsissolutions.fileprocess.FileProcessActor.{FileCreated, FileModified}
import io.methvin.better.files._

import scala.util.Try


class FileWatcher(val fileProcessActor:ActorRef, val dirpath:String)
{
    import SepsisUtils._

    val start = Try{config.getString("sepsis.file.begin")}.getOrElse("prueba")
    val end = Try{config.getString("sepsis.file.end")}.getOrElse(".csv")


    def isFilePruebas(filepath:String) = {
        filepath.startsWith(start) & filepath.endsWith(end)
    }

    val watcher = new RecursiveFileMonitor(File(dirpath)) {

        override def onCreate(file: File, count: Int): Unit = {
            sepsislog.info(s"CREATED $file")
            val filepath = file.path.getFileName.toString
            if(isFilePruebas(filepath)) {
                sepsislog.info("New .csv file uploaded")
                fileProcessActor ! FileCreated(file.path.toString)
            }else{
                sepsislog.info("File not .csv")
            }
        }

        override def onModify(file: File, count: Int): Unit = {
            //sepsislog.info(s"$file got modified $count times")
            val filepath = file.path.toString
            if(isFilePruebas(filepath)) {
                fileProcessActor ! FileModified(file.path.toString)
            }
        }

        override def onDelete(file: File, count: Int): Unit = {
            sepsislog.info(s"DELETED $file")
        }
    }


    def startWatcher() = {
        sepsislog.info(s"Watching dir: $dirpath")
        watcher.start()
     }
}
