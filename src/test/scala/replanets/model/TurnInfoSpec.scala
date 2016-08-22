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

  def buildCommands(cmd: PlayerCommand*) = MutableBuffer(cmd:_*)

  "set planet fcode command" should {
    "change planet fcode" in {
      val gs = gameState
      val planetId = PlanetId(175)
      val newFcode = Fcode("AA3")
      val commands = buildCommands(SetPlanetFcode(planetId, newFcode))
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
      val commands = buildCommands(SetShipFcode(shipId, newFcode))
      val turnInfo = TurnInfo(gs.rst, commands)

      val after = turnInfo.stateAfterCommands(gs.specs)

      after.ships(shipId).asInstanceOf[OwnShip].fcode should be (newFcode)
    }
  }

  "start ship construction command" should {
    val gs = gameState
    val planetId = PlanetId(303)
    val cmd = StartShipConstruction(
      planetId, HullId(33), EngineId(9), BeamId(8), 6, LauncherId(1), 4
    )
    val commands = buildCommands(cmd)
    val turnInfo = TurnInfo(gs.rst, commands)

    val after = turnInfo.stateAfterCommands(gs.specs)

    "set build order on starbase" in {
      after.bases(planetId).shipBeingBuilt should not be empty
      val order = after.bases(planetId).shipBeingBuilt.get
      order.hull.id should be (HullId(33))
      order.launcherCount should be (4)
      order.beamCount should be (6)
      order.engine.id should be (EngineId(9))
    }

    "subtract cost from planetary resources" in {
      val planet = after.planets(planetId)
      planet.surfaceMinerals.tritanium should be(1554)
      planet.surfaceMinerals.duranium should be(476)
      planet.surfaceMinerals.molybdenium should be(493)
      planet.money should be(0)
      planet.supplies should be(492)
    }
  }

  "stop ship construction command" when {
    "the build ship was in rst file" should {
      val gs = gameState
      val planetId = PlanetId(16)
      val cmd = StopShipConstruction(planetId)
      val ti = TurnInfo(gs.rst, buildCommands(cmd))

      val after = ti.stateAfterCommands(gs.specs)

      "should leave empty build order " in {
        gs.rst.bases(planetId).shipBeingBuilt should not be empty
        after.bases(planetId).shipBeingBuilt shouldBe empty
      }

      "should return resources to the planet" in {
        pending
      }
    }
  }
}
