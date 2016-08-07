package replanets.ui

import replanets.common.IonStorm
import replanets.model.Game
import replanets.ui.commands.Commands

import scalafx.Includes._
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.VBox
import scalafxml.core.{DependenciesByType, FXMLLoader, NoDependencyResolver}
import scala.reflect.runtime.universe._

/**
  * Created by mgirkin on 04/08/2016.
  */
class InformationView(game: Game, viewModel: ViewModel, commands: Commands) extends VBox {

  minWidth = 300
  fillWidth = true

  children = Seq(
    new Label("Infomation view"),
    new Button("sdf;kjsdfsdf") {
      maxWidth = Double.MaxValue
    }
  )
  styleClass = Seq("informationView")

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

  def showInfoAbout(mapObject: MapObject) = {
    mapObject.objectType match {
      case MapObjectType.Planet => showInfoAboutPlanet(mapObject)
      case MapObjectType.IonStorm => game.turnSeverData(viewModel.turnShown).ionStorms.find(_.id == mapObject.id).foreach(showInfoAboutIonStorm)
      case MapObjectType.Base => showInfoAboutBase(mapObject)
      case _ => children = Seq(Label("Not implemented yet"))
    }
  }

  private def showInfoAboutPlanet(mapObject: MapObject) = {
    planetInfoView.setPlanet(viewModel.turnShown, mapObject.id)
    children = Seq(planetInfoView.rootPane)
  }

  private def showInfoAboutIonStorm(storm: IonStorm): Unit = {
    ionStormInfoView.setData(storm)
    children = Seq(ionStormInfoView.rootPane)
  }

  private def showInfoAboutBase(mapObject: MapObject): Unit = {
    baseInfoView.setData(mapObject.id)
    children = Seq(baseInfoView.rootPane)
  }

}
