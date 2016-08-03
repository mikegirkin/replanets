package replanets

import java.nio.file.{Path, Paths}

import replanets.model.Game
import replanets.ui.ReplanetsPrimaryStage

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.{BorderPane, HBox}


object UiRunner extends JFXApp {
  val gamePath: Path = Paths.get(
    if(parameters.unnamed.nonEmpty) parameters.unnamed.head
    else "."
  )
  val absolutePath = gamePath.toAbsolutePath

  val game = Game.initFromDirectory(absolutePath)
  game.processRstFile(absolutePath.resolve("Player1.RST"))

  stage = new ReplanetsPrimaryStage(game)

  override def main(args: Array[String]): Unit = {
    if(args.length == 1 && args(0) == "console")
      ConsoleRunner.run
    else super.main(args)
  }
}
