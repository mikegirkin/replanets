package replanets.ui

import replanets.common.ExplosionRecord
import replanets.model.Game
import replanets.ui.viewmodels.ViewModel

import scalafx.scene.control.Label
import scalafx.scene.layout.Pane
import scalafxml.core.macros.sfxml

trait IExplosionInfoView {
  def rootPane: Pane
  def setData(explosion: ExplosionRecord): Unit
}

@sfxml
class ExplosionInfoView(
  val rootPane: Pane,
  val lblExplosionId: Label,
  val lblCoords: Label,

  val game: Game,
  val viewModel: ViewModel
) extends IExplosionInfoView {
  override def setData(explosion: ExplosionRecord): Unit = {
    lblExplosionId.text = explosion.id.toString
    lblCoords.text = s"at (${explosion.x}, ${explosion.y})"
  }
}
