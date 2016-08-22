package replanets.common

import java.nio.file.{Files, Path}

import play.api.libs.json._
import replanets.model.{PlayerCommand, Specs, TurnInfo}

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import replanets.model.JsonUtils._

class GameDatabase(gamePath: Path, val playingRace: RaceId) {
  val dbFoldername = "db"
  val dbDirectoryPath = gamePath.resolve(dbFoldername)
  def commandsFilePath(turnId: Int, raceId: Int) =
    dbDirectoryPath.resolve(turnId.toString).resolve(s"commands-$raceId.json")

  if(!Files.exists(dbDirectoryPath)) Files.createDirectory(dbDirectoryPath)

  def loadDb(specs: Specs): Map[TurnId, Map[RaceId, TurnInfo]] = {
    val rsts = loadRsts(specs)

    val result = rsts.map {
      case (turn, race, rst) => (turn, race, rst, loadCommands(turn, race))
    }.groupBy {
      case (turn, race, rst, commands) => turn
    }.map { case (turnNumber, content) =>
      (turnNumber, content.map { case (_, race, rst, commands) =>
        race -> (rst, commands)
      })
    }.map {
      case (turnNumber, data) => TurnId(turnNumber) -> data.map {
        case (raceId, (rst, commands)) => RaceId(raceId) -> TurnInfo(specs, rst).withCommands(commands: _*)
      }.toMap
    }

    result
  }

  def loadRsts(specs: Specs): Iterable[(Int, Int, ServerData)] = {
    val fileListInDb = Files.newDirectoryStream(dbDirectoryPath)
    fileListInDb.filter(f => Files.isDirectory(f))
      .flatMap { turnDirectory =>
      val filesInTurnDirectory = Files.newDirectoryStream(turnDirectory)
      filesInTurnDirectory.filter { file =>
        file.getFileName.toString.toLowerCase.endsWith("rst")
      }.map { file =>
        val rst = RstFileReader.read(file, playingRace, specs)
        (rst.generalInfo.turnNumber.toInt, rst.generalInfo.playerId.toInt, rst)
      }
    }

  }

  private case class PlayerActionsData(
    version: String,
    commands: Seq[PlayerCommand],
    additionalData: Seq[String]
  )

  private object PlayerActionsData {
    implicit val fmt = play.api.libs.json.Json.format[PlayerActionsData]
  }

  def saveCommands(turnId: TurnId, raceId: RaceId, commands: Seq[PlayerCommand]) = {
    val data = PlayerActionsData("v1", commands, Seq())
    val json = Json.prettyPrint(Json.toJson(data))
    Files.write(commandsFilePath(turnId.value, raceId.value), json.getBytes)
  }

  def loadCommands(turnId: Int, raceId: Int): Seq[PlayerCommand] = {
    val commandsFile = commandsFilePath(turnId, raceId)
    if(Files.exists(commandsFile)) {
      val bytes = Files.readAllBytes(commandsFile)
      val data = Json.parse(bytes).as[PlayerActionsData]
      data.commands
    } else {
      Seq()
    }
  }

  def importRstFile(rstFile: Path): Boolean = {
    if(!Files.exists(rstFile)) false
    else {
      val rst= RstFileReader.readGeneralInfo(rstFile)
      val turnNumber = rst.turnNumber
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