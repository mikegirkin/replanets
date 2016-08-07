package replanets.ui.commands

import replanets.model.Game
import replanets.ui.{MapObject, MapObjectType, ViewModel}

class SwitchToBaseViewCommand(
  game: Game,
  viewModel: ViewModel
) extends Command {
  override def execute(): Unit = {
    for(
      so <- viewModel.objectSelected;
      base <- if(so.objectType == MapObjectType.Planet) {
        game.turnSeverData(viewModel.turnShown).bases.find(b => b.baseId == so.id)
      } else None
    ) { if(base.owner == game.playingRace) viewModel.objectSelected = Some(MapObject(MapObjectType.Base, base.baseId, so.coords)) }
  }
}