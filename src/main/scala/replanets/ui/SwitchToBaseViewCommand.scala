package replanets.ui

import replanets.model.Game

class SwitchToBaseViewCommand(
  game: Game,
  viewModel: ViewModel
) extends Command {
  override def execute(): Unit = {
    val selectedObject = viewModel.objectSelected
    if(selectedObject.isEmpty) return
    val base = selectedObject.flatMap(so =>
      if(so.objectType == MapObjectType.Planet) {
        game.turnSeverData(viewModel.turnShown).bases.find(b => b.baseId == so.id)
      } else None
    )
    base.foreach(b => {
      if(b.owner == game.playingRace) {
        viewModel.objectSelected = Some(MapObject(MapObjectType.Base, b.baseId, selectedObject.get.coords))
      }
    })
  }
}