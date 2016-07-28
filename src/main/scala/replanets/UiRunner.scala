package replanets

import java.nio.file.{Path, Paths}

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

  stage = new PrimaryStage {
    title = "rePlanets"
    width = 400
    height = 400
    scene = new Scene{
      root = new BorderPane {
        center = new Label(absolutePath.toString)
        bottom = new HBox {
          padding = Insets(5)
          children = Seq(
            new Button {
              text = "F1 - Beams"
            },
            new Button("F2 - Torps"),
            new Button("F3 - Engines")
          )
        }
      }
    }
  }

  override def main(args: Array[String]): Unit = {
    if(args.length == 1 && args(0) == "console")
      ConsoleRunner.run
    else super.main(args)
  }
}
