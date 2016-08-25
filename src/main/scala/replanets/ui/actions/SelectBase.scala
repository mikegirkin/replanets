package replanets.ui.actions

import replanets.common.PlanetId
import replanets.model.Game
import replanets.ui.viewmodels.ViewModel
import replanets.ui.MapObject

class SelectBase(
  game: Game,
  viewModel: ViewModel
) extends UserAction {
  override def execute(): Unit = {
    for(
      so <- viewModel.selectedObject;
      //planet <- game.specs.map.planets.find(p => p.x == so.coords.x && p.y == so.coords.y);
      base <- game.turnInfo(viewModel.turnShown).stateAfterCommands.bases.get(PlanetId(so.id))
    ) {
      if(base.owner == game.playingRace) viewModel.selectedObject = Some(MapObject.forStarbase(game)(base))
    }
  }
}