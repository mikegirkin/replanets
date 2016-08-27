package replanets.ui

import replanets.common.Target
import replanets.model.Game
import replanets.ui.viewmodels.ViewModel

import scalafx.scene.control.Label
import scalafx.scene.layout.Pane
import scalafxml.core.macros.sfxml

trait ITargetInfoView {
  def rootPane: Pane
  def setData(target: Target)
}

@sfxml
class TargetInfoView(
  val rootPane: Pane,
  val lblShipId: Label,
  val lblHull: Label,
  val lblName: Label,
  val lblCoords: Label,
  val lblWarp: Label,
  val lblLoadMass: Label,
  val lblFullMass: Label,
  val lblMaxCrew: Label,
  val lblMineHit: Label,
  val lblWebHit: Label,
  val lblHullMass: Label,
  val lblEngineNumber: Label,
  val lblBeamNumber: Label,
  val lblLauncherNumber: Label,
  val lblMaxCargo: Label,
  val lblMaxFuel: Label,

  val game: Game,
  val viewModel: ViewModel
) extends ITargetInfoView {

  override def setData(target: Target): Unit  = {
    lblShipId.text = s"${game.races(target.owner.value - 1).adjective} ship ${target.id.value}"
    lblHull.text = target.hull.name
    lblName.text = target.name
    lblCoords.text = s"at (${target.x}, ${target.y})"
    lblWarp.text = target.warp.toString
    lblLoadMass.text = (target.fullMass - target.hull.mass).toString
    lblFullMass.text = target.fullMass.toString
    lblMaxCrew.text = target.hull.crewSize.toString
    lblMineHit.text = s"${game.specs.formulas.mineHitDamage(target.hull).toString} %"
    lblWebHit.text = s"${game.specs.formulas.webHitDamage(target.hull).toString} %"
    lblHullMass.text = target.hull.mass.toString
    lblEngineNumber.text = target.hull.enginesNumber.toString
    lblBeamNumber.text = target.hull.maxBeamWeapons.toString
    lblLauncherNumber.text = target.hull.maxTorpedoLaunchers.toString
    lblMaxCargo.text = target.hull.cargo.toString
    lblMaxFuel.text = target.hull.fuelTankSize.toString
  }
}
