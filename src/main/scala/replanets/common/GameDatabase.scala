package replanets.common

import java.nio.file.{Files, Path}

import replanets.model.TurnInfo

import scala.collection.JavaConversions._

/**
  * Created by mgirkin on 07/08/2016.
  */
class GameDatabase(gamePath: Path) {
  val dbDirectoryPath = gamePath.resolve("db")
  if(!Files.exists(dbDirectoryPath)) Files.createDirectory(dbDirectoryPath)

  def loadDb(): Map[Int, TurnInfo] = {
    val directoriesInDb = Files.newDirectoryStream(dbDirectoryPath)
    val rstFiles: Iterable[(Int, Int, RstFile)] = directoriesInDb.flatMap { turnDirectory =>
      val filesInTurnDirectory = Files.newDirectoryStream(turnDirectory)
      filesInTurnDirectory.filter { file =>
        file.getFileName.toString.toLowerCase.endsWith("rst")
      }.map { file =>
        val rst = RstFileReader.read(file)
        (rst.generalInfo.turnNumber.toInt, rst.generalInfo.playerId.toInt, rst)
      }
    }
    rstFiles.groupBy { case (turn, race, rst) => turn }
      .map { case (turnNumber, content) =>
        (turnNumber, content.map { case (_, race, rst) =>
          race -> rst
        })
      }.map { case (turnNumber, raceRsts) =>
        (turnNumber, TurnInfo(raceRsts.toMap))
      }
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
