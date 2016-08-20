package replanets.ui

import replanets.common.{Constants, PlanetId}
import replanets.model.Game
import replanets.ui.actions.Actions
import replanets.ui.viewmodels.ViewModel

import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.scene.control.{Label, ListCell, ListView}
import scalafx.scene.layout.{HBox, Pane, VBox}
import scalafxml.core.macros.sfxml

trait IBaseInfoView {
  def rootPane: Pane
  def setData(starbaseId: PlanetId): Unit
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

  val lvHulls: ListView[(Short, String)],
  val lvDrives: ListView[(Short, String)],
  val lvBeams: ListView[(Short, String)],
  val lvLaunchers: ListView[(Short, String)],

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

  private def lvCellFactory = { _: ListView[(Short, String)] =>
    new ListCell[(Short, String)] {
      item.onChange { (_, _, item) =>
        if(item == null) graphic = null
        else graphic = lvItem(item._1, item._2)
      }
    }
  }

  lvHulls.cellFactory = lvCellFactory
  lvBeams.cellFactory = lvCellFactory
  lvDrives.cellFactory = lvCellFactory
  lvLaunchers.cellFactory = lvCellFactory

  override def setData(starbaseId: PlanetId): Unit = {
    val base = game.turnSeverData(viewModel.turnShown).bases(starbaseId)
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

    lvHulls.items = ObservableBuffer(hulls)
    lvBeams.items = ObservableBuffer(beams)
    lvDrives.items = ObservableBuffer(drives)
    lvLaunchers.items = ObservableBuffer(launchers)
  }

  def handlePlanetButton(e: ActionEvent) = actions.selectPlanet.execute()

  def handleBuildShipButton(e: ActionEvent) = actions.showBuildShipView()
}
