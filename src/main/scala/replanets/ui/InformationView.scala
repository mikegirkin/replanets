package replanets.ui

import replanets.common.{ExplosionRecord, _}
import replanets.model.{Game, PlanetId, ShipId}
import replanets.ui.actions.Actions
import replanets.ui.viewmodels.ViewModel

import scala.reflect.runtime.universe._
import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{Label, ListCell, ListView}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{Pane, Priority, VBox}
import scalafx.scene.paint.Color

class InformationView(game: Game, viewModel: ViewModel, actions: Actions) extends VBox {
  import FXmlHelper._

  styleClass = Seq("informationView")

  val defaultColor = Color.LightGray
  val ownShipColor = Color.Cyan
  val enemyShipColor = Color.Red
  val mineFieldColor = Color.MediumPurple

  val objectDetailsView = new VBox {
    minHeight = 400
    //maxHeight = 400
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
        onMouseClicked = (e: MouseEvent) => {
          viewModel.selectedObject = Some(this.item.delegate.get())
        }
      }
    }
  }

  children = Seq(
    objectDetailsView,
    objectListView
  )


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

  val shipInfoView = loadFxml[IShipInfoView]("/ShipDetailsView.fxml", Map(
    typeOf[Game] -> game,
    typeOf[ViewModel] -> viewModel
  ))

  val targetInfoView = loadFxml[ITargetInfoView]("/TargetInfoView.fxml", Map(
    typeOf[Game] -> game,
    typeOf[ViewModel] -> viewModel
  ))

  val contactInfoView = loadFxml[IContactInfoView]("/ContactInfoView.fxml", Map(
    typeOf[Game] -> game,
    typeOf[ViewModel] -> viewModel
  ))

  val minefieldInfoView = loadFxml[IMinefieldInfoView]("/MinefieldInfoView.fxml", Map(
    typeOf[Game] -> game,
    typeOf[ViewModel] -> viewModel
  ))

  val explosionInfoView = loadFxml[IExplosionInfoView]("/ExplosionInfoView.fxml", Map(
    typeOf[Game] -> game,
    typeOf[ViewModel] -> viewModel
  ))

  def onSelectedObjectChanged(selectedObject: MapObject): Unit = {
    showListInfoForPoint(selectedObject.coords)
    objectListView.selectionModel.delegate.get().select(selectedObject)
    showInfoAbout(selectedObject)
  }

  private def showInfoAbout(mapObject: MapObject) = {
    mapObject match {
      case _ : MapObject.Planet => showInfoAboutPlanet(mapObject)
      case _ : MapObject.IonStorm => game.turnSeverData(viewModel.turnShown).ionStorms.find(_.id == mapObject.id).foreach(showInfoAboutIonStorm)
      case _ : MapObject.Starbase => showInfoAboutBase(mapObject)
      case _ : MapObject.OwnShip => showInfoAboutShip(mapObject.id)
      case _ : MapObject.Target => showInfoAboutShip(mapObject.id)
      case _ : MapObject.Minefield => game.turnSeverData(viewModel.turnShown).mineFields.find(_.id == mapObject.id).foreach(showInfoAboutMinefield)
      case _ : MapObject.Explosion => game.turnSeverData(viewModel.turnShown).explosions.find(_.id == mapObject.id).foreach(showInfoAboutExplosion)
      case _ => setDetailsView(new VBox {
        children = Seq(new Label("Not implemented yet"))
      })
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
        shipInfoView.setData(x)
        setDetailsView(shipInfoView.rootPane)
      case x: Target =>
        targetInfoView.setData(x)
        setDetailsView(targetInfoView.rootPane)
      case x: Contact =>
        contactInfoView.setData(x)
        setDetailsView(contactInfoView.rootPane)
    }
  }

  private def showInfoAboutMinefield(mineField: MineFieldRecord) = {
    minefieldInfoView.setData(mineField)
    setDetailsView(minefieldInfoView.rootPane)
  }

  private def showInfoAboutPlanet(mapObject: MapObject) = {
    planetInfoView.setPlanet(viewModel.turnShown, mapObject.id)
    setDetailsView(planetInfoView.rootPane)
  }

  private def showInfoAboutIonStorm(storm: IonStorm): Unit = {
    ionStormInfoView.setData(storm)
    setDetailsView(ionStormInfoView.rootPane)
  }

  private def showInfoAboutBase(mapObject: MapObject): Unit = {
    baseInfoView.setData(PlanetId(mapObject.id))
    setDetailsView(baseInfoView.rootPane)
  }

  def showInfoAboutExplosion(explosionRecord: ExplosionRecord): Unit = {
    explosionInfoView.setData(explosionRecord)
    setDetailsView(explosionInfoView.rootPane)
  }

  private def setDetailsView(view: Pane): Unit = {
    objectDetailsView.children = view
  }



}