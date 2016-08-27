package replanets.model.commands

import org.scalatest.{Matchers, WordSpec}
import replanets.common.{PlanetId, TurnId}
import replanets.model.TestGame_10

class BuildFactoriesSpec extends WordSpec with Matchers with TestGame_10 {
  "Build factories command" should {
    "correctly detect if it replaces another" in {
      val cmd1 = new BuildFactories(PlanetId(495), 1)
      val cmd2 = new BuildFactories(PlanetId(495), 2)
      val cmd3 = new BuildFactories(PlanetId(293), 1)

      cmd1.isReplacableBy(cmd2) shouldBe true
      cmd1.isReplacableBy(cmd3) shouldBe false
    }

  }

  "Build factories command" when {
    "executed against state" should {
      val hwPlanetId = PlanetId(495)
      val ti = game.turnInfo(TurnId(11))

      "correctly detect if it adds something to state" in {
        val cmd1 = new BuildFactories(PlanetId(495), 1)
        cmd1.isAddDiffToInitialState(ti.initialState, specs) shouldBe true

        val cmd2 = new BuildFactories(PlanetId(245), 0)
        cmd2.isAddDiffToInitialState(ti.initialState, specs) shouldBe false
      }

      "build given number of factories" in {
        val cmd = new BuildFactories(hwPlanetId, 2)

        ti.addCommand(cmd)

        val stateAfter = ti.stateAfterCommands

        val planet = stateAfter.planets(hwPlanetId)
        planet.factoriesNumber shouldBe 277
        planet.money shouldBe 13340
        planet.supplies shouldBe 398
      }

      "build the max possible number of factories limited by population" in {
        val cmd = BuildFactories(hwPlanetId, 42)

        ti.addCommand(cmd)

        val planet = ti.stateAfterCommands.planets(hwPlanetId)
        planet.factoriesNumber shouldBe 279
        planet.money shouldBe 13334
        planet.supplies shouldBe 396
      }

      "build the max possible number of factories limited by money" in {
        val planetId = PlanetId(38)
        val cmd = BuildFactories(planetId, 42)

        ti.addCommand(cmd)

        val planet = ti.stateAfterCommands.planets(planetId)
        planet.factoriesNumber shouldBe 20
        planet.money shouldBe 9
        planet.supplies shouldBe 0
      }
    }
  }
}
