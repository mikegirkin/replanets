package replanets.ui

import replanets.common.IonStorm
import replanets.model.Game
import replanets.ui.commands.Commands

import scalafx.Includes._
import scalafx.scene.control.{Label, ListCell, ListView}
import scalafx.scene.layout.{Pane, Priority, VBox}
import scalafxml.core.{DependenciesByType, FXMLLoader, NoDependencyResolver}
import scala.reflect.runtime.universe._
import scalafx.beans.value.ObservableValue
import scalafx.collections.ObservableBuffer

/**
  * Created by mgirkin on 04/08/2016.
  */
class InformationView(game: Game, viewModel: ViewModel, commands: Commands) extends VBox {


  styleClass = Seq("informationView")

  val objectDetailsView = new VBox {
    minHeight = 550
    maxHeight = 550
  }

  val objectListView = new ListView[(MapObject, String)] {
    maxHeight = Double.MaxValue
    minHeight = 100
    vgrow = Priority.Always
    cellFactory = { _ =>
      new ListCell[(MapObject, String)] {
        styleClass = Seq("objectsListCell")
        item.onChange { (_, _, item) =>
          if(item == null) text = null
          else text = item._2
        }
      }
    }
    selectionModel().selectedItem.onChange { (mo, _, _) =>
      if(mo.value != null) viewModel.objectSelected = Some(mo.value._1)
    }
  }

  children = Seq(
    objectDetailsView,
    objectListView
  )

  val ionStormInfoView = {
    val loader = new FXMLLoader(getClass.getResource("/IonStormInfoView.fxml"), NoDependencyResolver)
    loader.load()
    loader.getController[IIonStormInformationView]
  }

  val planetInfoView = {
    val loader = new FXMLLoader(
      getClass.getResource("/PlanetInfoView.fxml"),
      new DependenciesByType(Map(
        typeOf[Commands] -> commands
      ))
    )
    loader.load()
    loader.getController[IPlanetInfoView].setGameModel(game)
  }

  val baseInfoView = {
    val loader = new FXMLLoader(
      getClass.getResource("/BaseInfoView.fxml"),
      new DependenciesByType(Map(
        typeOf[Commands] -> commands,
        typeOf[Game] -> game,
        typeOf[ViewModel] -> viewModel
      ))
    )
    loader.load()
    loader.getController[IBaseInfoView]
  }

  def onSelectedObjectChanged(selectedObject: MapObject): Unit = {
    showInfoAbout(selectedObject)
    showListInfoForPoint(selectedObject.coords)
  }

  private def showInfoAbout(mapObject: MapObject) = {
    mapObject.objectType match {
      case MapObjectType.Planet => showInfoAboutPlanet(mapObject)
      case MapObjectType.IonStorm => game.turnSeverData(viewModel.turnShown).ionStorms.find(_.id == mapObject.id).foreach(showInfoAboutIonStorm)
      case MapObjectType.Base => showInfoAboutBase(mapObject)
      case MapObjectType.Ship => showInfoAboutShip(mapObject.id)
      case _ => children = Seq(Label("Not implemented yet"))
    }
  }

  def showListInfoForPoint(coords: IntCoords): Unit = {
    val items = MapObject.findAtCoords(game, viewModel.turnShown)(coords)
    val viewItems = ObservableBuffer[(MapObject, String)](items)
    objectListView.items = viewItems
  }

  private def showInfoAboutShip(shipId: Int) = {
    val ship = game.turnSeverData(viewModel.turnShown).ships.find(_.shipId == shipId)
    println(ship)
  }

  private def showInfoAboutPlanet(mapObject: MapObject) = {
    planetInfoView.setPlanet(viewModel.turnShown, mapObject.id)
    setDetailsView(Some(planetInfoView.rootPane))
  }

  private def showInfoAboutIonStorm(storm: IonStorm): Unit = {
    ionStormInfoView.setData(storm)
    setDetailsView(Some(ionStormInfoView.rootPane))
  }

  private def showInfoAboutBase(mapObject: MapObject): Unit = {
    baseInfoView.setData(mapObject.id)
    setDetailsView(Some(baseInfoView.rootPane))
  }

  private def setDetailsView(view: Option[Pane]): Unit = {
    objectDetailsView.children = view.toSeq
  }

}
