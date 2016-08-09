package replanets.ui.actions

import replanets.model.Game
import replanets.ui.viewmodels.ViewModel
import replanets.ui.{MapObject, MapObjectType}

class SelectBase(
  game: Game,
  viewModel: ViewModel
) extends UserAction {
  override def execute(): Unit = {
    for(
      so <- viewModel.selectedObject;
      base <- if(so.objectType == MapObjectType.Planet) {
        game.turnSeverData(viewModel.turnShown).bases.find(b => b.baseId == so.id)
      } else None
    ) { if(base.owner == game.playingRace) viewModel.selectedObject = Some(MapObject(MapObjectType.Base, base.baseId, so.coords)) }
  }
}