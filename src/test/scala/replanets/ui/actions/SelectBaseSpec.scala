package replanets.ui.actions

import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, WordSpec}
import replanets.common.{BaseRecord, RaceId, RstFile, TurnId}
import replanets.model.{ClusterMap, Game, Planet, TurnInfo}
import replanets.ui.viewmodels.ViewModel
import replanets.ui.{MapObject, MapObjectType}

import scala.collection.mutable

class SelectBaseSpec extends WordSpec with Matchers with MockitoSugar {
  "Sets desired base as selected" in {
    val planet = Planet(10, 350, 213, "Tatooin")
    val turnNumber = 15
    val playingRace: Short = 2

    val base = mock[BaseRecord]
    when(base.baseId).thenReturn(planet.id)
    when(base.owner).thenReturn(playingRace)

    val rst = mock[RstFile]
    when(rst.bases).thenReturn(IndexedSeq(base))

    val game = mock[Game]
    when(game.turns).thenReturn(
      Map(TurnId(turnNumber) -> Map(
        RaceId(playingRace) -> TurnInfo(rst, mutable.Buffer())
      )))
    when(game.map).thenReturn(ClusterMap(4000, 4000, IndexedSeq(planet)))
    when(game.turnSeverData(TurnId(turnNumber))).thenReturn(rst)
    when(game.playingRace).thenReturn(RaceId(playingRace))

    val viewModel = ViewModel(
      TurnId(turnNumber),
      Some(MapObject(MapObjectType.Planet, planet.id, planet.coords))
    )

    val action = new SelectBase(game, viewModel)
    action.execute()

    viewModel.selectedObject should be  (Some(MapObject(MapObjectType.Base, base.baseId, planet.coords)))
  }
}
