package replanets.common

import org.scalatest.{Matchers, WordSpec}
import replanets.model.{SetPlanetFcode, SetShipFcode}

/**
  * Created by mgirkin on 09/08/2016.
  */
class CommandsSpec extends WordSpec with Matchers {
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
  }
}
