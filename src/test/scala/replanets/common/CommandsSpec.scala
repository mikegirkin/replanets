package replanets.common

import org.scalatest.{Matchers, WordSpec}
import replanets.model.{SetPlanetFcode, SetShipFcode}

/**
  * Created by mgirkin on 09/08/2016.
  */
class CommandsSpec extends WordSpec with Matchers {
  "SetPlanetFcode" should {
    "Correctly detect if other command is a replacement" in {
      val cmd = SetPlanetFcode(213, Fcode("AAB"))
      val otherPlanetCmd = SetPlanetFcode(111, Fcode.random())
      val shipCmd = SetShipFcode(123, Fcode("HYP"))
      val replacementCmd = SetPlanetFcode(213, Fcode("NUK"))

      cmd.isReplacableBy(otherPlanetCmd) should be(false)
      cmd.isReplacableBy(shipCmd) should be(false)
      cmd.isReplacableBy(replacementCmd) should be(true)
    }
  }
}
