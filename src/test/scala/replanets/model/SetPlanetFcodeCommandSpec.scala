package replanets.model

import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Matchers, WordSpec}
import replanets.common._
import replanets.model.commands.{SetPlanetFcode, SetShipFcode}

class SetPlanetFcodeCommandSpec extends WordSpec with Matchers with MockitoSugar {
  "SetPlanetFcode" should {
    "Correctly detect if other command is a replacement" in {
      val cmd = SetPlanetFcode(PlanetId(213), Fcode("AAB"))
      val otherPlanetCmd = SetPlanetFcode(PlanetId(111), Fcode.random())
      val shipCmd = SetShipFcode(ShipId(232), Fcode("HYP"))
      val replacementCmd = SetPlanetFcode(PlanetId(213), Fcode("NUK"))

      cmd.isReplacableBy(otherPlanetCmd) should be(false)
      cmd.isReplacableBy(shipCmd) should be(false)
      cmd.isReplacableBy(replacementCmd) should be(true)
    }

    "Correctly detect that it doesn't chages initial state" in {
      val planetId1 = PlanetId(213)
      val planetId2 = PlanetId(345)
      val nonChanging = SetPlanetFcode(planetId1, Fcode("AAB"))
      val changing = SetPlanetFcode(planetId2, Fcode("NUK"))
      val turnId = TurnId(8)
      val raceId = RaceId(6)
      val specs = mock[Specs]
      val planet1 = mock[PlanetRecord]
      when(planet1.fcode).thenReturn(Fcode("AAB"))
      val planet2 = mock[PlanetRecord]
      when(planet2.fcode).thenReturn(Fcode("sdd"))
      val serverData = mock[ServerData]
      when(serverData.planets).thenReturn(
        Map(
          planetId1 -> planet1,
          planetId2 -> planet2
        )
      )
      val turns: Map[TurnId, Map[RaceId, TurnInfo]] = Map(turnId ->
        Map(raceId -> TurnInfo(specs, serverData).withCommands(nonChanging))
      )

      val game = mock[Game]
      when(game.turns).thenReturn(turns)

      nonChanging.isAddDiffToInitialState(serverData) should be (false)
      changing.isAddDiffToInitialState(serverData) should be (true)
    }
  }

}

