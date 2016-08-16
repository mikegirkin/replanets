package replanets.ui

import replanets.common._
import replanets.model.{Game, ShipId}
import replanets.ui.actions.Actions
import replanets.ui.viewmodels.ViewModel

import scala.reflect.runtime.universe._
import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{Label, ListCell, ListView}
import scalafx.scene.layout.{Pane, Priority, VBox}
import scalafx.scene.paint.Color
import scalafxml.core.{DependenciesByType, FXMLLoader}

/**
  * Created by mgirkin on 04/08/2016.
  */
class InformationView(game: Game, viewModel: ViewModel, actions: Actions) extends VBox {

  styleClass = Seq("informationView")

  val defaultColor = Color.LightGray
  val ownShipColor = Color.Cyan
  val enemyShipColor = Color.Red
  val mineFieldColor = Color.MediumPurple

  val objectDetailsView = new VBox {
    minHeight = 500
    maxHeight = 500
  }

  val objectListView = new ListView[MapObject] {
    maxHeight = Double.MaxValue
    minHeight = 100
    vgrow = Priority.Always
    cellFactory = { _ =>
      new ListCell[MapObject] {
        styleClass = Seq("objectsListCell")
        item.onChange { (_, _, item) =>
          if(item == null) text = null
          else {
            val color = item match {
              case _ : MapObject.OwnShip => ownShipColor
              case _ : MapObject.Target => enemyShipColor
              case _ : MapObject.Minefield => mineFieldColor
              case _ => defaultColor
            }
            textFill = color
            text = item.displayName
          }
        }
      }
    }
    selectionModel().selectedItem.onChange { (mo, _, _) =>
      if(mo.value != null) viewModel.selectedObject = Some(mo.value)
    }
  }

  children = Seq(
    objectDetailsView,
    objectListView
  )

  private def loadFxml[TController](resouce: String, dependencies: Map[Type, Any] = Map()): TController = {
    val loader = new FXMLLoader(
      getClass.getResource(resouce),
      new DependenciesByType(dependencies)
    )
    loader.load()
    loader.getController[TController]
  }

  val ionStormInfoView = loadFxml[IIonStormInformationView]("/IonStormInfoView.fxml")

  val planetInfoView = loadFxml[IPlanetInfoView]("/PlanetInfoView.fxml", Map(
    typeOf[Game] -> game,
    typeOf[ViewModel] -> viewModel,
    typeOf[Actions] -> actions
  ))

  val baseInfoView = loadFxml[IBaseInfoView]("/BaseInfoView.fxml", Map(
    typeOf[Actions] -> actions,
    typeOf[Game] -> game,
    typeOf[ViewModel] -> viewModel
  ))

  val shipDetailsView = loadFxml[IShipInfoView]("/ShipDetailsView.fxml", Map(
    typeOf[Game] -> game,
    typeOf[ViewModel] -> viewModel
  ))

  val targetDetailsView = loadFxml[ITargetInfoView]("/TargetInfoView.fxml", Map(
    typeOf[Game] -> game,
    typeOf[ViewModel] -> viewModel
  ))

  def onSelectedObjectChanged(selectedObject: MapObject): Unit = {
    showListInfoForPoint(selectedObject.coords)
    showInfoAbout(selectedObject)
  }

  private def showInfoAbout(mapObject: MapObject) = {
    mapObject match {
      case _ : MapObject.Planet => showInfoAboutPlanet(mapObject)
      case _ : MapObject.IonStorm => game.turnSeverData(viewModel.turnShown).ionStorms.find(_.id == mapObject.id).foreach(showInfoAboutIonStorm)
      case _ : MapObject.Starbase => showInfoAboutBase(mapObject)
      case _ : MapObject.OwnShip => showInfoAboutShip(mapObject.id)
      case _ : MapObject.Target => showInfoAboutShip(mapObject.id)
      case _ => setDetailsView(Some(new VBox {
        children = Seq(new Label("Not implemented yet"))
      }))
    }
  }

  def showListInfoForPoint(coords: IntCoords): Unit = {
    val items = MapObject.findAtCoords(game, viewModel.turnShown)(coords)
    val viewItems = ObservableBuffer[MapObject](items)
    objectListView.items = viewItems
  }

  private def showInfoAboutShip(shipId: Int) = {
    val ship = game.turnSeverData(viewModel.turnShown).ships(ShipId(shipId))
    ship match {
      case x: OwnShip =>
        shipDetailsView.setData(x)
        setDetailsView(Some(shipDetailsView.rootPane))
      case x: Target =>
        targetDetailsView.setData(x)
        setDetailsView(Some(targetDetailsView.rootPane))
      case x: Contact =>
    }
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