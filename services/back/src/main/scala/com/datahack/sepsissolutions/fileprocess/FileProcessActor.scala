package com.datahack.sepsissolutions.fileprocess

import akka.actor.{Actor, ActorRef}
import com.datahack.sepsissolutions.SepsisUtils
import com.datahack.sepsissolutions.dao.DaoActor.InsertPruebasIfNotExists

import scala.concurrent.duration._

object FileProcessActor{

  case class FileCreated(filepath:String)
  case class FileModified(filepath:String)

  case class CheckFile(filepath:String)
}

class FileProcessActor(daoActor:ActorRef,scoreActor:ActorRef)
  extends Actor
{

  import SepsisUtils._
  import FileProcessActor._

  var filemap  = scala.collection.mutable.Map.empty[String,Boolean]


  override def receive = {


    case FileCreated(filepath) => {
      sepsislog.info(s"File $filepath marked as modified")
      filemap(filepath) = true
      context.system.scheduler.scheduleOnce(1 second, self, CheckFile(filepath))
    }

    case FileModified(filepath) => {
      sepsislog.info(s"File $filepath marked as modified")
      filemap(filepath) = true
    }

    case CheckFile(filepath) => {
      sepsislog.info(s"CHECKING FILE $filepath")
      if(filemap(filepath)){
        context.system.scheduler.scheduleOnce(1 second, self, CheckFile(filepath))
        filemap(filepath) = false
      }else{
        sepsislog.info(s"Processing file $filepath")
        val pruebas = ProcessFile.processFile(filepath)
        daoActor.tell(InsertPruebasIfNotExists(pruebas), scoreActor)
      }
    }
  }

}
