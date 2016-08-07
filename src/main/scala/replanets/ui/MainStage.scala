package replanets.ui

import replanets.model.Game
import replanets.ui.commands.{Commands, SwitchToBaseViewCommand, SwitchToPlanetViewCommand}

import scalafx.Includes._
import scalafx.application.JFXApp.PrimaryStage
import scalafx.event.ActionEvent
import scalafx.scene.control.Button
import scalafx.scene.input.KeyEvent
import scalafx.scene.layout.{BorderPane, HBox}
import scalafx.scene.{Node, Scene}

class MainStage(game: Game, viewModel: ViewModel) extends PrimaryStage {

  private val mainLayout = new BorderPane {
    minWidth = 600
  }

  private val commands = new Commands(
    new SwitchToBaseViewCommand(game, viewModel),
    new SwitchToPlanetViewCommand(game, viewModel)
  )
  private val messageView = new MessagesView(game.turns(viewModel.turnShown).serverReceiveState.rstFiles(game.playingRace).messages)
  private val mapView = new MapView(game, viewModel)
  private val informationView = new InformationView(game, viewModel, commands)

  scene = new Scene {
    stylesheets += getClass.getResource("/styles.css").toExternalForm
    minWidth = 600
    minHeight = 600
    root = mainLayout

    onKeyPressed = (e:KeyEvent) => handleKeyPressed(e)
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
    viewModel.objectSelected.foreach(x => informationView.onSelectedObjectChanged(x))
  }

  private def setMainView(view: Node) = {
    mainLayout.center = view
  }

  private def handleKeyPressed(e: KeyEvent): Unit = {
    e.code.name match {
      case "B" => commands.switchToBaseViewCommand.execute()
      case "P" => commands.switchToPlanetViewCommand.execute()
      case _ =>
    }
  }


}



