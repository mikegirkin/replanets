package replanets.ui

import replanets.common.Contact
import replanets.model.Game
import replanets.ui.viewmodels.ViewModel

import scalafx.scene.control.Label
import scalafx.scene.layout.Pane
import scalafxml.core.macros.sfxml

trait IContactInfoView {
  def rootPane: Pane
  def setData(contact: Contact)
}

@sfxml
class ContactInfoView(
  val rootPane: Pane,
  val lblRace: Label,
  val lblShipId: Label,
  val lblCoords: Label,
  val lblMass: Label,

  val game: Game,
  val viewModel: ViewModel
) extends IContactInfoView {
  override def setData(contact: Contact): Unit = {
    lblRace.text = game.races(contact.owner.value - 1).adjective
    lblShipId.text = contact.id.value.toString
    lblCoords.text = s"at (${contact.coords.x}, ${contact.coords.y})"
    lblMass.text = contact.mass.toString
  }
}
