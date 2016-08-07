package replanets.ui

import replanets.model.Game
import replanets.ui.commands.Commands

import scalafx.event.ActionEvent
import scalafx.scene.control.Label
import scalafx.scene.layout.{Pane, VBox}
import scalafxml.core.macros.sfxml

trait IBaseInfoView {
  def rootPane: Pane
  def setData(starbaseId: Int): Unit
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

  val commands: Commands,
  val game: Game,
  val viewModel: ViewModel
) extends IBaseInfoView {

  override def setData(starbaseId: Int): Unit = {
    val base = game.turnSeverData(viewModel.turnShown).bases.find(_.baseId == starbaseId)
    base.foreach { b =>
      lblStarbaseId.text = b.baseId.toString
      lblDefense.text = b.defences.toString
      lblDamage.text = b.damage.toString
      lblFighters.text = b.fightersNumber.toString
      lblPrimaryOrder.text = b.primaryOrder.toString
      lblEngines.text = b.engineTech.toString
      lblHulls.text = b.hullsTech.toString
      lblBeams.text = b.beamTech.toString
      lblTorpedoes.text = b.torpedoTech.toString
    }
  }

  def handlePlanetButton(e: ActionEvent) = commands.switchToPlanetViewCommand.execute()
}
