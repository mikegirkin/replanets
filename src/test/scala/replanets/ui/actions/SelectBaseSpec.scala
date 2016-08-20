package replanets.ui.actions

import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, WordSpec}
import replanets.common._
import replanets.model._
import replanets.ui.viewmodels.ViewModel
import replanets.ui.MapObject

import scala.collection.mutable

class SelectBaseSpec extends WordSpec with Matchers with MockitoSugar {
  "Sets desired base as selected" in {
    val planet = Planet(10, 350, 213, "Tatooin")
    val turnNumber = 15
    val playingRace = RaceId(2)

    val base = mock[Starbase]
    when(base.id).thenReturn(PlanetId(planet.id))
    when(base.owner).thenReturn(playingRace)

    val rst = mock[ServerData]
    when(rst.bases).thenReturn(Map(PlanetId(planet.id) -> base))

    val game = mock[Game]
    when(game.turns).thenReturn(
      Map(TurnId(turnNumber) -> Map(
        playingRace -> TurnInfo(rst, mutable.Buffer())
      )))
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
