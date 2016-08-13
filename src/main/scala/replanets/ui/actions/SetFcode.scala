package replanets.ui.actions

import replanets.common.Fcode
import replanets.model.{Game, PlanetId, SetPlanetFcode}
import replanets.ui.MapObjectType
import replanets.ui.viewmodels.ViewModel

/**
  * Created by mgirkin on 09/08/2016.
  */
class SetFcode(game: Game, viewModel: ViewModel) {
  def execute(fcode: Fcode): Unit = {
    viewModel.selectedObject.foreach { so =>
      so.objectType match {
        case MapObjectType.Planet =>
          val command = SetPlanetFcode(PlanetId(so.id), fcode)
          game.addCommand(command)
          viewModel.objectChanged.fire((MapObjectType.Planet, so.id))
        case _ =>
      }
    }
  }
}
