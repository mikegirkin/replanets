package replanets.model

import java.nio.file.Paths

import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Matchers, WordSpec}
import replanets.common._
import replanets.model.commands.{SetColonistTax, SetPlanetFcode}

class SetColonistTaxSpec extends WordSpec with Matchers with MockitoSugar {
  "SetColonistTaxRateCommand" should {
    "correctly detect other command as a replacement" in {
      val planetId = PlanetId(213)
      val cmd = SetColonistTax(planetId, 17)
      val cmd2 = SetPlanetFcode(planetId, Fcode("AAS"))
      val cmd3 = SetColonistTax(planetId, 12)

      cmd.isReplacableBy(cmd2) shouldBe false
      cmd.isReplacableBy(cmd3) shouldBe true
    }

    "correctly detects if there is any change to initial state" in {
      val planetId = PlanetId(213)
      val cmd = SetColonistTax(planetId, 17)
      val cmd2 = SetColonistTax(planetId, 12)
      val planet = mock[PlanetRecord]
      when(planet.colonistTax).thenReturn(12)
      val initialState = mock[ServerData]
      when(initialState.planets).thenReturn(
        Map(
          PlanetId(213) -> planet
        )
      )

      cmd.isAddDiffToInitialState(initialState) shouldBe true
      cmd2.isAddDiffToInitialState(initialState) shouldBe false
    }

    "actually sets the tax" in {
      val directory = Paths.get("testfiles/testgame-1")
      val specs = Specs.fromDirectory(directory)
      val path = directory.resolve("db/1/player1.rst")
      val rst = RstFileReader.read(path, RaceId(1), specs)

      val ti = TurnInfo(specs, rst)
      val planetId = PlanetId(213)
      val newTaxValue = 17
      val cmd = SetColonistTax(planetId, 17)

      cmd.apply(rst).planets(planetId).colonistTax shouldBe newTaxValue
    }

    "tax is limited by [0, 100] interval" in {
      val directory = Paths.get("testfiles/testgame-1")
      val specs = Specs.fromDirectory(directory)
      val path = directory.resolve("db/1/player1.rst")
      val rst = RstFileReader.read(path, RaceId(1), specs)

      val ti = TurnInfo(specs, rst)
      val planetId = PlanetId(213)
      val newTaxValue = 17

      SetColonistTax(planetId, -500).apply(rst).planets(planetId).colonistTax shouldBe 0
      SetColonistTax(planetId, 100500).apply(rst).planets(planetId).colonistTax shouldBe 100
    }
  }
}
