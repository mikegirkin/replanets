package replanets.ui

import replanets.common.{Constants, IonStorm, PlanetRecord}
import replanets.model.Game

import scalafx.scene.control.{Button, Control, Label}
import scalafx.scene.layout.VBox
import scalafxml.core.{DependenciesByType, FXMLLoader, NoDependencyResolver}

/**
  * Created by mgirkin on 04/08/2016.
  */
class InformationView(game: Game, viewModel: ViewModel) extends VBox {

  minWidth = 300
  fillWidth = true

  children = Seq(
    new Label("Infomation view"),
    new Button("sdf;kjsdfsdf") {
      maxWidth = Double.MaxValue
    }

  )

  val ionStormInfoView = {
    val loader = new FXMLLoader(getClass.getResource("/IonStormInfoView.fxml"), new DependenciesByType(Map()))
    loader.load()
    loader.getController[IIonStormInformationView]
  }

  val planetInfoView = {
    val loader = new FXMLLoader(getClass.getResource("/PlanetInfoView.fxml"), NoDependencyResolver)
    loader.load()
    loader.getController[IPlanetInfoView].setGameModel(game)
  }

  def showInfoAbout(mapObject: MapObject) = {
    mapObject.objectType match {
      case MapObjectType.Planet => showInfoAboutPlanet(mapObject)
      case MapObjectType.IonStorm => game.turnSeverData(viewModel.turnShown).ionStorms.find(_.id == mapObject.id).foreach(showInfoAboutIonStorm)
      case _ => children = Seq(Label("Not implemented yet"))
    }
  }

  private def showInfoAboutPlanet(mapObject: MapObject) = {
    planetInfoView.setPlanetId(viewModel.turnShown, mapObject.id)
    children = Seq(planetInfoView.rootPane)
  }

  private def showInfoAboutIonStorm(storm: IonStorm): Unit = {
    ionStormInfoView.setData(storm)
    children = Seq(ionStormInfoView.rootPane)
  }
}
