package replanets.ui

import replanets.model.Game

import scalafx.scene.layout.Pane
import scalafxml.core.macros.sfxml

/**
  * Created by mgirkin on 07/08/2016.
  */
trait IShipDetailsView {
  def rootPane: Pane
}

@sfxml
class ShipDetailsView(
  val rootPane: Pane,

  val game: Game,
  val viewModel: ViewModel
) extends IShipDetailsView {

}
