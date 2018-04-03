package replanets.model.commands

import org.scalatest.{Matchers, WordSpec}
import replanets.common.{PlanetId, TurnId}
import replanets.model.TestGame_10
import replanets.model.commands.v0.{BuildDefences, BuildFactories}

class BuildDefencesSpec extends WordSpec with Matchers with TestGame_10 {
  "Build defences command" should {
    "correctly determine if it is replacable by another" in {
      BuildDefences(PlanetId(495), 5).isReplacableBy(BuildDefences(PlanetId(495), 7)) shouldBe true
      BuildDefences(PlanetId(495), 5).isReplacableBy(BuildDefences(PlanetId(256), 1)) shouldBe false
      BuildDefences(PlanetId(495), 5).isReplacableBy(BuildFactories(PlanetId(495), 1)) shouldBe false
    }
  }

  "Build defences command" when {
    "applied to the state" should {
      val ti = game.turnInfo(TurnId(11))
      val hwPlanetId = PlanetId(495)

      "correctly determine if it add something to the initial state" in {
        BuildDefences(hwPlanetId, 5).isAddDiffToInitialState(ti.initialState, specs) shouldBe true
        BuildDefences(hwPlanetId, 0).isAddDiffToInitialState(ti.initialState, specs) shouldBe false
      }

      "build given number of defences" in {
        val stateAfter = BuildDefences(hwPlanetId, 5).apply(ti.initialState, specs)

        val planet = stateAfter.planets(hwPlanetId)
        planet.defencesNumber shouldBe (100 + 5)
        planet.supplies shouldBe (400 - 5)
        planet.money shouldBe (13346 - 5 * 10)
      }

      "build max possible defences when limited by population" in {
        val stateAfter = BuildDefences(hwPlanetId, 200).apply(ti.initialState, specs)

        val planet = stateAfter.planets(hwPlanetId)
        planet.defencesNumber shouldBe (229)
        planet.supplies shouldBe (400 - 129)
        planet.money shouldBe (13346 - 129 * 10)
      }

      "build max possible defences when limited by resources" in {
        val stateAfter = BuildDefences(PlanetId(38), 20).apply(ti.initialState, specs)

        val planet = stateAfter.planets(PlanetId(38))
        planet.defencesNumber shouldBe 4
        planet.supplies shouldBe 5
        planet.money shouldBe 0
      }
    }
  }
}
