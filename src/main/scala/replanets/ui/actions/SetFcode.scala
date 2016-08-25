package replanets.ui.actions

import replanets.common.{Fcode, PlanetId}
import replanets.model.Game
import replanets.model.commands.SetPlanetFcode
import replanets.ui.MapObject
import replanets.ui.viewmodels.ViewModel

class SetFcode(game: Game, viewModel: ViewModel) {
  def execute(fcode: Fcode): Unit = {
    viewModel.selectedObject.foreach { so =>
      so match {
        case x : MapObject.Planet =>
          val command = SetPlanetFcode(PlanetId(so.id), fcode)
          game.addCommand(command)
          viewModel.objectChanged.fire(x)
        case _ =>
      }
    }
  }
}
