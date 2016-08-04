package replanets.ui

import replanets.common.{Constants, PlanetRecord}
import replanets.model.Game

import scalafx.scene.control.{Control, Label}
import scalafx.scene.layout.VBox

/**
  * Created by mgirkin on 04/08/2016.
  */
class InformationView(game: Game, viewModel: ViewModel) extends VBox {

  minWidth = 300

  children = Seq(
    new Label("Infomation view")
  )

  def showInfoAbout(mapObject: MapObject) = {
    mapObject.objectType match {
      case MapObjectType.Planet => showInfoAboutPlanet(mapObject)
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

    val planetInfo = game.turns.last.serverReceiveState.rstFiles(1).planets.find(_.planetId == mapObject.id)

    children = commonViewItems ++ planetInfo.map {p => knownViewItems(p)}.getOrElse(Seq())
  }
}
