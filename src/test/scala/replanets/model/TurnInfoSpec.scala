package replanets.model

import java.nio.file.Paths

import org.scalatest.{Matchers, WordSpec}
import replanets.common._
import scala.collection.mutable.{Buffer => MutableBuffer}

class TurnInfoSpec extends WordSpec with Matchers {

  "set planet fcode command" should {
    "change planet fcode" in {
      val directory = Paths.get("./testfiles/testgame-4")
      val path = directory.resolve("db/38/player4.rst")
      val specs = Specs.fromDirectory(directory)
      val rst = RstFileReader.read(path, RaceId(4), specs)
      val planetId = PlanetId(175)
      val newFcode = Fcode("AA3")
      val commands: MutableBuffer[PlayerCommand] = MutableBuffer(SetPlanetFcode(planetId, newFcode))
      val turnInfo = TurnInfo(rst, commands)

      val after = turnInfo.stateAfterCommands(specs)

      after.planets(planetId).fcode should be (newFcode)
    }
  }
}
