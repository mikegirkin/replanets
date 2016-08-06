package replanets.ui

import replanets.model.Game

import scalafx.scene.control.Label
import scalafx.scene.layout.{Pane, VBox}
import scalafxml.core.macros.sfxml

trait IBaseInfoView {
  def rootPane: Pane
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
  val game: Game
) extends IBaseInfoView {

}
