package replanets

import java.nio.file._

import replanets.common.GameDatabase
import replanets.model.Game
import replanets.ui.MainStage
import replanets.ui.viewmodels.ViewModel

import scala.collection.JavaConversions._
import scalafx.application.JFXApp

object UiRunner extends JFXApp {
  val raceNumber = parameters.unnamed(0).toInt
  val gameDirectory = parameters.unnamed(1)

  println(s"Starting for race $raceNumber in game directory $gameDirectory")
  val gamePath: Path = Paths.get(gameDirectory)
  val absolutePath = gamePath.toAbsolutePath
  val gameDb = new GameDatabase(absolutePath, raceNumber)

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
    if(args.length == 1 && args(0) == "console")
      ConsoleRunner.run
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
