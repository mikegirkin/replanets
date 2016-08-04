package replanets.ui

import replanets.model.Game

import scalafx.Includes._
import scalafx.application.JFXApp.PrimaryStage
import scalafx.event.ActionEvent
import scalafx.scene.control.Button
import scalafx.scene.layout.{BorderPane, VBox}
import scalafx.scene.{Node, Scene}

class ReplanetsPrimaryStage(game: Game) extends PrimaryStage {

  private val mainLayout = new BorderPane {
    minWidth = 600
  }

  private val messageView = new MessagesView(game.turns.last.serverReceiveState.rstFiles(game.playingRace).messages)
  private val mapView = new MapView(game)

  scene = new Scene {
    minWidth = 600
    root = mainLayout
  }
  title = "rePlanets"
  width = 600
  height = 400
  mainLayout.center = mapView
  mainLayout.bottom = new Toolbar {
    override def onMessages(e: ActionEvent): Unit = setMainView(messageView)
    override def onMap(e: ActionEvent): Unit = setMainView(mapView)
  }
  mainLayout.right = new VBox {
    children = Seq(
      new Button {
        text = "Zoom In"
        onAction = (e: ActionEvent) => mapView.zoomIn()
      },
      new Button {
        text = "Zoom out"
        onAction = (e: ActionEvent) => mapView.zoomOut()
      }
    )
  }

  private def setMainView(view: Node) = {
    mainLayout.center = view
  }

}

