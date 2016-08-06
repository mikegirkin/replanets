package replanets.ui

import replanets.model.Game
import replanets.ui.ScalafxHelpers.loadController

import scalafx.scene.control.Label
import scalafx.scene.layout.{Pane, VBox}
import scalafxml.core.macros.sfxml
/**
  * Created by mgirkin on 06/08/2016.
  */
trait IPlanetInfoView {
  def rootPane: VBox
  def setGameModel(model: Game): IPlanetInfoView
  def setPlanetId(turnId: Int, planetId: Int)
}

@sfxml
class PlanetInfoView(
  val rootPane: VBox,
  private val lblName: Label,
  private val lblWhen: Label,
  private val lblOwner: Label,
  private val lblFcode: Label,
  private val lblClimate: Label,
  private val pnNatives: Pane,
  private val pnColonists: Pane
) extends IPlanetInfoView {

  var game:Game = null

  private val nativesView = loadController[INativesView]("/NativesView.fxml")
  private val colonistsView = loadController[IColonistsView]("/ColonistsView.fxml")

  pnNatives.children = nativesView.root
  pnColonists.children = colonistsView.root

  override def setGameModel(model: Game): IPlanetInfoView ={
    this.game = model
    this
  }

  override def setPlanetId(turnId: Int, planetId: Int): Unit = {
    val data = game.turnSeverData(turnId)
    lblName.text = s"${game.map.planets(planetId).name} ($planetId)"
    lblWhen.text = "(now)"
    data.planets.find(_.planetId == planetId).foreach(p => {
      lblOwner.text = game.races(p.ownerId - 1).shortname
      lblFcode.text = p.fcode
      lblClimate.text = p.temperature.toString
      nativesView.setData(p)
      colonistsView.setData(p)
    })
  }
}