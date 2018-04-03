package replanets.model.commands

import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Matchers, WordSpec}
import replanets.common.{PlanetId, TurnId}
import replanets.model.TestGame_10
import replanets.model.commands.v0.SetNativeTax

class SetNativeTaxSpec extends WordSpec with Matchers with MockitoSugar with TestGame_10 {
  "SetNativeTax command" should {
    "actually set native tax" in {
      val ti = game.turnInfo(TurnId(8))
      val planetId = PlanetId(219)
      val taxValue = 17

      SetNativeTax(planetId, taxValue).apply(ti.initialState, specs).planets(planetId).nativeTax shouldBe 17
    }

    "tax in limited by [0, 100]" in {
      val ti = game.turnInfo(TurnId(8))
      val planetId = PlanetId(219)

      SetNativeTax(planetId, -123).apply(ti.initialState, specs).planets(planetId).nativeTax shouldBe 0
      SetNativeTax(planetId, 123).apply(ti.initialState, specs).planets(planetId).nativeTax shouldBe 100
    }

    "correctly determine if the command replaceable by another" in {
      val planetId = PlanetId(219)
      val cmd1 = SetNativeTax(planetId, 7)
      val cmd2 = SetNativeTax(planetId, 13)
      val cmd3 = SetNativeTax(PlanetId(32), 43)

      cmd1.isReplacableBy(cmd2) shouldBe true
      cmd1.isReplacableBy(cmd3) shouldBe false
    }

    "correctly determine if the command changes initial state" in {
      val ti = game.turnInfo(TurnId(8))
      val planetId = PlanetId(219)
      val initialTaxValue = ti.initialState.planets(planetId).nativeTax

      SetNativeTax(planetId, initialTaxValue).isAddDiffToInitialState(ti.initialState, specs) shouldBe false
      SetNativeTax(planetId, initialTaxValue + 3).isAddDiffToInitialState(ti.initialState, specs) shouldBe true
    }
  }
}
