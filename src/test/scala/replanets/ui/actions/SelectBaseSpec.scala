package replanets.ui.actions

import org.mockito.Mockito._
import org.mockito.Matchers.any
import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, WordSpec}
import replanets.common._
import replanets.model._
import replanets.ui.viewmodels.ViewModel
import replanets.ui.MapObject

class SelectBaseSpec extends WordSpec with Matchers with MockitoSugar with TestGame_1 {
  "Sets desired base as selected" in {
    val planet = game.specs.map.planets(213 - 1)
    val turn = TurnId(1)
    val playingRace = RaceId(1)

    val viewModel = ViewModel(
      turn,
      Some(MapObject.forPlanet(planet))
    )

    val action = new SelectBase(game, viewModel)
    action.execute()

    viewModel.selectedObject.get.id should be (planet.id)
    viewModel.selectedObject.get shouldBe a [MapObject.Starbase]
  }
}
