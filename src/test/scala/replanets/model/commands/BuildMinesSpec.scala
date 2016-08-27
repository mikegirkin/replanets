package replanets.model.commands

import org.scalatest.{Matchers, WordSpec}
import replanets.common.{PlanetId, TurnId}
import replanets.model.TestGame_10

class BuildMinesSpec extends WordSpec with Matchers with TestGame_10 {
  "Build mines command" should {
    "correctly determine if it is replacable by another command" in {
      val cmd1 = BuildMines(PlanetId(495), 5)
      val cmd2 = BuildMines(PlanetId(495), 9)
      val cmd3 = BuildMines(PlanetId(213), 10)

      cmd1.isReplacableBy(cmd2) shouldBe true
      cmd2.isReplacableBy(cmd3) shouldBe false
    }
  }

  "Build mines command" when {
    "exeuted against state" should {
      val ti = game.turnInfo(TurnId(11))
      val hwPlanetId = PlanetId(495)

      "correctly determine if it adds to the initial state" in {
        BuildMines(hwPlanetId, 3).isAddDiffToInitialState(ti.initialState, specs) shouldBe true
        BuildMines(PlanetId(245), 0).isAddDiffToInitialState(ti.initialState, specs) shouldBe false
      }

      "build given number of mines" in {
        val stateAfter = BuildMines(hwPlanetId, 10).apply(ti.initialState, specs)

        val planet = stateAfter.planets(hwPlanetId)
        planet.minesNumber shouldBe 210
        planet.supplies shouldBe 390
        planet.money shouldBe 13306
      }

      "build max mines when limited by population" in {
        val stateAfter = BuildMines(hwPlanetId, 300).apply(ti.initialState, specs)

        val planet = stateAfter.planets(hwPlanetId)
        planet.minesNumber shouldBe 379
        planet.supplies shouldBe (400 - 179)
        planet.money shouldBe (13346 - 179 * 4)
      }

      "build max possible mines when limited by resources" in {
        val stateAfter = BuildMines(PlanetId(38), 20).apply(ti.initialState, specs)

        val planet = stateAfter.planets(PlanetId(38))
        planet.minesNumber shouldBe 9
        planet.supplies shouldBe (10 - 9)
        planet.money shouldBe (39 - 9 * 4)
      }
    }
  }
}
