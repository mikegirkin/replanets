package replanets.model

import java.nio.file.Paths

import org.scalatest.{Matchers, WordSpec}
import replanets.common._
import scala.collection.mutable.{Buffer => MutableBuffer}

class TurnInfoSpec extends WordSpec with Matchers {

  def gameState = new {
    val directory = Paths.get("./testfiles/testgame-4")
    val path = directory.resolve("db/38/player4.rst")
    val specs = Specs.fromDirectory(directory)
    val rst = RstFileReader.read(path, RaceId(4), specs)
  }

  "set planet fcode command" should {
    "change planet fcode" in {
      val gs = gameState
      val planetId = PlanetId(175)
      val newFcode = Fcode("AA3")
      val commands: MutableBuffer[PlayerCommand] = MutableBuffer(SetPlanetFcode(planetId, newFcode))
      val turnInfo = TurnInfo(gs.rst, commands)

      val after = turnInfo.stateAfterCommands(gs.specs)

      after.planets(planetId).fcode should be (newFcode)
    }
  }

  "set ship fcode command" should {
    "change ship fcode" in {
      val gs = gameState
      val shipId = ShipId(1)
      val newFcode = Fcode("082")
      val commands: MutableBuffer[PlayerCommand] = MutableBuffer(SetShipFcode(shipId, newFcode))
      val turnInfo = TurnInfo(gs.rst, commands)

      val after = turnInfo.stateAfterCommands(gs.specs)

      after.ships(shipId).asInstanceOf[OwnShip].fcode should be (newFcode)
    }
  }
}
