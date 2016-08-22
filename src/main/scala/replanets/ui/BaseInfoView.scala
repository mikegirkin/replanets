package replanets.ui

import replanets.common.Constants
import replanets.model.{Game, Starbase}
import replanets.ui.actions.Actions
import replanets.ui.viewmodels.ViewModel

import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.scene.control.{Label, ListCell, ListView}
import scalafx.scene.layout.{HBox, Pane, VBox}
import scalafxml.core.macros.sfxml

trait IBaseInfoView {
  def rootPane: Pane
  def setData(base: Starbase): Unit
}

@sfxml
class BaseInfoView(
  val rootPane: VBox,
  val lblStarbaseId: Label,
  val lblDefense: Label,
  val lblDamage: Label,
  val lblFighters: Label,
  val lblPrimaryOrder: Label,
  val lblEngines: Label,
  val lblHulls: Label,
  val lblBeams: Label,
  val lblTorpedoes: Label,

  val lvStorage: ListView[(Int, String)],

  val actions: Actions,
  val game: Game,
  val viewModel: ViewModel
) extends IBaseInfoView {

  private def lvItem(amount: Int, name: String) = new HBox {
    children = Seq(
      new Label {
        text = amount.toString
        styleClass = Seq("storageListCount")
      },
      new Label {
        text = name
      })
  }

  private def lvCellFactory = { _: ListView[(Int, String)] =>
    new ListCell[(Int, String)] {
      item.onChange { (_, _, item) =>
        if(item == null) graphic = null
        else graphic = lvItem(item._1, item._2)
      }
    }
  }

  lvStorage.cellFactory = lvCellFactory

  override def setData(base: Starbase): Unit = {
    lblStarbaseId.text = base.id.value.toString
    lblDefense.text = base.defences.toString
    lblDamage.text = base.damage.toString
    lblFighters.text = base.fightersNumber.toString
    lblPrimaryOrder.text = Constants.baseMissions(base.primaryOrder)
    lblEngines.text = base.engineTech.toString
    lblHulls.text = base.hullsTech.toString
    lblBeams.text = base.beamTech.toString
    lblTorpedoes.text = base.torpedoTech.toString

    val hulls = base.storedHulls
      .toSeq
      .map{ case (hullId, hullCount) => (hullCount, game.specs.hullSpecs(hullId.value - 1).name) } //TODO: Check if stored hulls are correctly calculated

    val beams = base.storedBeams.zipWithIndex
      .filter { case (beamCount, idx) => beamCount > 0 }
      .map { case (beamCount, idx) => (beamCount, game.specs.beamSpecs(idx).name) }

    val drives = base.storedEngines.zipWithIndex
      .filter { case (driveCount, idx) => driveCount > 0 }
      .map { case (driveCount, idx) => (driveCount, game.specs.engineSpecs(idx).name) }

    val launchers = base.storedLaunchers.zipWithIndex
      .filter { case (launcherCount, idx) => launcherCount > 0 }
      .map { case (launcherCount, idx) => (launcherCount, game.specs.torpSpecs(idx).name) }

    val torpedoes = base.storedTorpedoes.zipWithIndex
      .filter { case (torpedoCount, idx) => torpedoCount > 0 }
      .map { case (torpedoCount, idx) => (torpedoCount, s"${game.specs.torpSpecs(idx).name} torpedoes")}

    lvStorage.items = ObservableBuffer(hulls ++ beams ++ drives ++ launchers ++ torpedoes)
  }

  def handlePlanetButton(e: ActionEvent) = actions.selectPlanet.execute()

  def handleBuildShipButton(e: ActionEvent) = actions.showBuildShipView()
}
