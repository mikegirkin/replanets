package replanets.common

import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.file.{Files, Path}

import replanets.model.{PlayerCommand, TurnInfo}
import replanets.model.JsonUtils._
import play.api.libs.json._
import play.api.libs.json.Json._

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer

/**
  * Created by mgirkin on 07/08/2016.
  */
class GameDatabase(gamePath: Path, val playingRace: Int) {
  val dbFoldername = "db"
  val dbDirectoryPath = gamePath.resolve(dbFoldername)
  val commandsFilePath = dbDirectoryPath.resolve(s"commands-$playingRace.json")

  if(!Files.exists(dbDirectoryPath)) Files.createDirectory(dbDirectoryPath)

  def loadDb(): Map[Int, TurnInfo] = {
    val rsts = loadRsts()
    val commands = loadCommands()
    val result = rsts.groupBy { case (turn, race, rst) => turn }
      .map { case (turnNumber, content) =>
        (turnNumber, content.map { case (_, race, rst) =>
          race -> rst
        })
      }.map { case (turnNumber, raceRsts) =>
        val cmds = commands.get(turnNumber)
        if(cmds.isEmpty) (turnNumber, TurnInfo(raceRsts.toMap))
        else {
          val ti = TurnInfo(raceRsts.toMap, ArrayBuffer(cmds.get:_*))
          (turnNumber, ti)
        }
      }
    result
  }

  def loadRsts(): Iterable[(Int, Int, RstFile)] = {
    val fileListInDb = Files.newDirectoryStream(dbDirectoryPath)
    fileListInDb.filter(f => Files.isDirectory(f))
      .flatMap { turnDirectory =>
      val filesInTurnDirectory = Files.newDirectoryStream(turnDirectory)
      filesInTurnDirectory.filter { file =>
        file.getFileName.toString.toLowerCase.endsWith("rst")
      }.map { file =>
        val rst = RstFileReader.read(file)
        (rst.generalInfo.turnNumber.toInt, rst.generalInfo.playerId.toInt, rst)
      }
    }

  }

  case class commandRow(turn: Int, commands: ArrayBuffer[PlayerCommand])
  implicit val commandRowFormat = format[commandRow]

  def saveCommands(commands: Map[Int, ArrayBuffer[PlayerCommand]]) = {
    val json = Json.stringify(Json.toJson(commands.map {x => commandRow(x._1, x._2)}))
    Files.write(commandsFilePath, json.getBytes)
  }

  def loadCommands(): Map[Int, Seq[PlayerCommand]] = {
    if(Files.exists(commandsFilePath)) {
      val bytes = Files.readAllBytes(commandsFilePath)
      Json.parse(bytes).as[Seq[commandRow]].map { x => (x.turn, x.commands) }.toMap
    } else
      Map()
  }

  def importRstFile(rstFile: Path): Boolean = {
    if(!Files.exists(rstFile)) false
    else {
      val rst= RstFileReader.read(rstFile)
      val turnNumber = rst.generalInfo.turnNumber
      val turnDurectoryPath = dbDirectoryPath.resolve(turnNumber.toString)
      val rstFileName = turnDurectoryPath.resolve(rstFile.getFileName.toString.toLowerCase())
      val dbRstFilePath = turnDurectoryPath.resolve(rstFileName)
      if(!Files.exists(turnDurectoryPath)) Files.createDirectory(turnDurectoryPath)
      if(Files.exists(dbRstFilePath)) Files.delete(dbRstFilePath)
      Files.copy(rstFile, dbRstFilePath)
      true
    }
  }
}
