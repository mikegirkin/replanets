package replanets.ui

import replanets.model.Game

import scalafx.Includes._
import scalafx.application.JFXApp.PrimaryStage
import scalafx.event.ActionEvent
import scalafx.scene.control.Button
import scalafx.scene.layout.{BorderPane, HBox}
import scalafx.scene.{Node, Scene}

class MainStage(game: Game, viewModel: ViewModel) extends PrimaryStage {

  private val mainLayout = new BorderPane {
    minWidth = 600
  }

  private val messageView = new MessagesView(game.turns(viewModel.turnShown).serverReceiveState.rstFiles(game.playingRace).messages)
  private val mapView = new MapView(game, viewModel)
  private val informationView = new InformationView(game, viewModel)

  scene = new Scene {
    stylesheets += getClass.getResource("/styles.css").toExternalForm
    minWidth = 600
    root = mainLayout
  }
  title = "rePlanets"
  width = 1280
  height = 960
  mainLayout.center = mapView
  mainLayout.bottom = new Toolbar {
    override def onMessages(e: ActionEvent): Unit = setMainView(messageView)
    override def onMap(e: ActionEvent): Unit = setMainView(mapView)
  }
  mainLayout.right = informationView
  mainLayout.top = new HBox {
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

  viewModel.selectedObjectChaged += { () =>
    viewModel.objectSelected.foreach(x => informationView.showInfoAbout(x))
  }

  private def setMainView(view: Node) = {
    mainLayout.center = view
  }

}



