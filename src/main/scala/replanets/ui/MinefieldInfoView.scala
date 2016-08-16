package replanets.ui

import replanets.common.MineFieldRecord
import replanets.model.Game
import replanets.ui.viewmodels.ViewModel

import scalafx.scene.control.Label
import scalafx.scene.layout.Pane
import scalafxml.core.macros.sfxml

trait IMinefieldInfoView {
  def rootPane: Pane
  def setData(minefield: MineFieldRecord)
}

@sfxml
class MinefieldInfoView(
  val rootPane: Pane,
  val lblRace: Label,
  val lblMinefieldId: Label,
  val lblTurnObserved: Label,
  val lblCenter: Label,
  val lblRadius: Label,
  val lblUnits: Label,
  val lblUnitsNextTurn: Label,

  val game: Game,
  val viewModel: ViewModel
) extends IMinefieldInfoView {

  override def setData(minefield: MineFieldRecord): Unit = {
    lblRace.text = game.races(minefield.owner - 1).adjective
    lblMinefieldId.text = minefield.id.toString
    //TODO: lblTurnObserved
    lblCenter.text = s"(${minefield.x}, ${minefield.y})"
    lblRadius.text = minefield.radius.toString
    val units = game.formulas.unitsInMinefieldByRadius(minefield.radius)
    lblUnits.text = units.toString
    lblUnitsNextTurn.text = game.formulas.minefieldUnitsNextTurn(units).toString
  }

}
