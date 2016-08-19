package replanets.ui

import replanets.model.{BaseId, Game}
import replanets.ui.actions.{Actions, SelectBase, SelectPlanet, SetFcode}
import replanets.ui.viewmodels.{CurrentView, ViewModel}

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

  private val actions = new Actions(
    new SelectBase(game, viewModel),
    new SelectPlanet(game, viewModel),
    new SetFcode(game, viewModel),
    showBuildShipView = () => viewModel.currentView = CurrentView.BuildShip
  )

  private val messageView = new MessagesView(game.turns(viewModel.turnShown)(game.playingRace).rst.messages)
  private val mapView = new MapView(game, viewModel)
  private val informationView = new InformationView(game, viewModel, actions)
  private val buildShipView = new BuildShipView(game, viewModel)

  private def showMapView() = {
    mainLayout.center = mapView
    mainLayout.right = informationView
  }

  private def showBuildShipView() = {
    viewModel.selectedObject.foreach { b =>
      buildShipView.setData(BaseId(b.id.toShort))
      mainLayout.center = buildShipView
      mainLayout.right = null
    }
  }

  viewModel.currentViewChanged += { (Unit) =>
    viewModel.currentView match {
      case CurrentView.Map => showMapView()
      case CurrentView.BuildShip => showBuildShipView()
    }
  }

  scene = new Scene {
    stylesheets += getClass.getResource("/styles.css").toExternalForm
    minWidth = 600
    minHeight = 500
    root = mainLayout

    onKeyPressed = (e:KeyEvent) => handleKeyPressed(e)
  }
  title = "rePlanets"
  width = 1280
  minHeight = 650
  height = 700

  mainLayout.bottom = new Toolbar {
    override def onMessages(e: ActionEvent): Unit = setMainView(messageView)
    override def onMap(e: ActionEvent): Unit = setMainView(mapView)
  }


  showMapView()
  mainLayout.top = new HBox {
    children = Seq(
      new Button {
        text = "Zoom In"
        onAction = (e: ActionEvent) => mapView.zoomIn()
      },
      new Button {
        text = "Zoom out"
        onAction = (e: ActionEvent) => mapView.zoomOut()
      },
      new Button {
        text = "Save"
        onAction = (e: ActionEvent) => handleSave()
      },
      new Button {
        text = "Write trn"
        onAction = (e: ActionEvent) => game.writeTrnForLastTurn()
      }
    )
  }

  viewModel.selectedObjectChaged += { (Unit) =>
    viewModel.selectedObject.foreach(x => informationView.onSelectedObjectChanged(x))
  }

  private def setMainView(view: Node) = {
    mainLayout.center = view
  }

  private def handleKeyPressed(e: KeyEvent): Unit = {
    e.code.name match {
      case "B" => actions.selectStarbase.execute()
      case "P" => actions.selectPlanet.execute()
      case _ =>
    }
  }

  private def handleSave() = {
    game.saveCommands()
  }


}



