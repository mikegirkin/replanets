package replanets.common

import java.nio.file.{Files, Path}

import replanets.model.JsonUtils._
import play.api.libs.json.Json._
import play.api.libs.json._
import replanets.model.{PlayerCommand, TurnInfo}

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Created by mgirkin on 07/08/2016.
  */
class GameDatabase(gamePath: Path, val playingRace: Int) {
  val dbFoldername = "db"
  val dbDirectoryPath = gamePath.resolve(dbFoldername)
  def commandsFilePath(turnId: Int, raceId: Int) =
    dbDirectoryPath.resolve(turnId.toString).resolve(s"commands-$raceId.json")

  if(!Files.exists(dbDirectoryPath)) Files.createDirectory(dbDirectoryPath)

  def loadDb(): Map[TurnId, Map[RaceId, TurnInfo]] = {
    val rsts = loadRsts()

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
        case (raceId, (rst, commands)) => RaceId(raceId) -> TurnInfo(rst, ArrayBuffer(commands: _*))
      }.toMap
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

  def saveCommands(turnId: TurnId, raceId: RaceId, commands: mutable.Buffer[PlayerCommand]) = {
    val json = Json.stringify(Json.toJson(commands))
    Files.write(commandsFilePath(turnId.value, raceId.value), json.getBytes)
  }

  def loadCommands(turnId: Int, raceId: Int): Seq[PlayerCommand] = {
    val commandsFile = commandsFilePath(turnId, raceId)
    if(Files.exists(commandsFile)) {
      val bytes = Files.readAllBytes(commandsFile)
      Json.parse(bytes).as[Seq[PlayerCommand]]
    } else
      Seq()
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