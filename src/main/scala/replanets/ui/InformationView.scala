package replanets.ui

import replanets.common.{Constants, IonStorm, PlanetRecord}
import replanets.model.Game

import scalafx.scene.control.{Button, Control, Label}
import scalafx.scene.layout.VBox
import scalafxml.core.{DependenciesByType, FXMLLoader}

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

  def showInfoAbout(mapObject: MapObject) = {
    mapObject.objectType match {
      case MapObjectType.Planet => showInfoAboutPlanet(mapObject)
      case MapObjectType.IonStorm => game.turnSeverData(viewModel.turnShown).ionStorms.find(_.id == mapObject.id).foreach(showInfoAboutIonStorm)
      case _ => children = Seq(Label("Not implemented yet"))
    }
  }

  private def showInfoAboutPlanet(mapObject: MapObject) = {
    def commonViewItems = Seq(
      new Label(s"Planet ${mapObject.id}"),
      new Label(game.map.planets(mapObject.id).name),
      new Label("(now)")
    )

    def knownViewItems(p: PlanetRecord): Seq[Control] = {
      Seq(
        new Label(s"Race: ${game.races(p.ownerId - 1).shortname}"),
        new Label(s"FCode: ${p.fcode}"),
        new Label(s"Climate: ${p.temperature}"),
        new Label(s"Natives:"),
        new Label(s"  ${Constants.natives(p.nativeRace)}"),
        new Label(s"  ${Constants.nativeGovernments(p.nativeGovernment)}"),
        new Label(s"  Pop: ${p.nativeClans} cl"),
        new Label(s"  Taxes: ${p.nativeTax} %"),
        new Label(s"  Happy: ${p.nativeHappiness} %"),
        new Label(s"Colonists:"),
        new Label(s"  Pop: ${p.colonistClans} cl"),
        new Label(s"  Taxes: ${p.colonistTax} %"),
        new Label(s"  Happy: ${p.colonistHappiness} %"),
        new Label(s"Mines: ${p.minesNumber}/???"),
        new Label(s"Factories: ${p.factoriesNumber}/???"),
        new Label(s"Defenses: ${p.defencesNumber}/???"),
        new Label(s"Supplies: ${p.supplies}"),
        new Label(s"Money: ${p.money}"),
        new Label(s"Income: ???"),
        new Label("Minerals:")
      )
    }

    val planetInfo = game.turns(viewModel.turnShown).serverReceiveState.rstFiles(1).planets.find(_.planetId == mapObject.id)

    children = commonViewItems ++ planetInfo.map {p => knownViewItems(p)}.getOrElse(Seq())
  }

  private def showInfoAboutIonStorm(storm: IonStorm): Unit = {
    ionStormInfoView.setData(storm)
    children = Seq(ionStormInfoView.rootPane)
  }
}
