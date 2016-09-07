package replanets

import java.io.File

import com.typesafe.config.ConfigFactory
import java.nio.file._

import replanets.common.{GameDatabase, RaceId}
import replanets.model.Game
import replanets.ui.MainStage
import replanets.ui.viewmodels.ViewModel

import scala.collection.JavaConversions._
import scalafx.application.JFXApp


object UiRunner extends JFXApp {
  val raceNumber = parameters.unnamed(0).toInt
  val gameDirectory = parameters.unnamed(1)

  val conf = ConfigFactory.parseFile(new File("config/replanets.conf"))
  val loglevel = conf.getString("loglevel")

  println(s"Starting for race $raceNumber in game directory $gameDirectory")
  val gamePath: Path = Paths.get(gameDirectory)
  val absolutePath = gamePath.toAbsolutePath
  val gameDb = new GameDatabase(absolutePath, RaceId(raceNumber))

  val rstFiles = rstFilesInGameDirectory(absolutePath)
  rstFiles.foreach { file =>
    val result = gameDb.importRstFile(file)
    if(result) Files.delete(file)
  }


  val game = Game(absolutePath)(gameDb)

  val lastTurnNumber = game.lastTurn
  val viewModel = ViewModel(lastTurnNumber, None)

  stage = new MainStage(game, viewModel)

  override def main(args: Array[String]): Unit = {
    if(args.length > 0 && args(0) == "console") {
      if(args(1) == "untrn") {
        ConsoleRunner.untrn(args(2))
      } else if(args(1) == "racehulls") {
        ConsoleRunner.raceHulls(args(2).toInt, args(3))
      } else if(args(1) == "unrst") {
        if(args(2) == "bases") ConsoleRunner.rstBases(args(3), args(4))
      }

    }
    else super.main(args)
  }

  private def rstFilesInGameDirectory(dir: Path): Iterable[Path] = {
    Files.newDirectoryStream(dir)
      .filter(path => {
        val filename = path.getFileName.toString.toLowerCase
        filename.startsWith("player") && filename.endsWith("rst")
      })
  }

}
