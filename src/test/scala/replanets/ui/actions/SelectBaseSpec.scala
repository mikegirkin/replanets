package replanets.ui.actions

import org.mockito.Mockito._
import org.mockito.Matchers.any
import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, WordSpec}
import replanets.common._
import replanets.model._
import replanets.ui.viewmodels.ViewModel
import replanets.ui.MapObject

class SelectBaseSpec extends WordSpec with Matchers with MockitoSugar {
  "Sets desired base as selected" in {
    val planet = Planet(10, 350, 213, "Tatooin")
    val turnNumber = 15
    val playingRace = RaceId(2)

    val specs = mock[Specs]

    val base = mock[Starbase]
    when(base.id).thenReturn(PlanetId(planet.id))
    when(base.owner).thenReturn(playingRace)

    val rst = mock[ServerData]
    when(rst.bases).thenReturn(Map(PlanetId(planet.id) -> base))

    val game = mock[Game]
    when(game.turnInfo(any[TurnId]()))
      .thenReturn(TurnInfo(specs, rst))
    when(game.map).thenReturn(ClusterMap(4000, 4000, IndexedSeq(planet)))
    when(game.turnSeverData(TurnId(turnNumber))).thenReturn(rst)
    when(game.playingRace).thenReturn(playingRace)

    val viewModel = ViewModel(
      TurnId(turnNumber),
      Some(MapObject.forPlanet(planet))
    )

    val action = new SelectBase(game, viewModel)
    action.execute()

    viewModel.selectedObject should be (Some(MapObject.forStarbase(game)(base)))
  }
}
