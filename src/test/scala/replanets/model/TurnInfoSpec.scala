package replanets.model

import java.nio.file.Paths

import org.scalatest.{Matchers, WordSpec}
import replanets.common._
import replanets.model.commands.{SetPlanetFcode, SetShipFcode, StartShipConstruction, StopShipConstruction}

class TurnInfoSpec extends WordSpec with Matchers {

  def gameState = new {
    val directory = Paths.get("./testfiles/testgame-4")
    val path = directory.resolve("db/38/player4.rst")
    val specs = Specs.fromDirectory(directory)(THostFormulas, new Missions(RaceId(4), THost))
    val rst = RstFileReader.read(path, RaceId(4), specs)
  }

  "set planet fcode command" should {
    "change planet fcode" in {
      val gs = gameState
      val planetId = PlanetId(175)
      val newFcode = Fcode("AA3")
      val commands = SetPlanetFcode(planetId, newFcode)
      val turnInfo = TurnInfo(gs.specs, gs.rst).withCommands(commands)

      val after = turnInfo.stateAfterCommands

      after.planets(planetId).fcode should be (newFcode)
    }
  }

  "set ship fcode command" should {
    "change ship fcode" in {
      val gs = gameState
      val shipId = ShipId(1)
      val newFcode = Fcode("082")
      val commands = SetShipFcode(shipId, newFcode)
      val turnInfo = TurnInfo(gs.specs, gs.rst).withCommands(commands)

      val after = turnInfo.stateAfterCommands

      after.ships(shipId).asInstanceOf[OwnShip].fcode should be (newFcode)
    }

    "replace another command for the same ship" in {
      val gs = gameState
      val shipId = ShipId(1)
      val newFcode = Fcode("082")
      val turnInfo = TurnInfo(gs.specs, gs.rst).withCommands(
        SetShipFcode(shipId, Fcode("mkt")),
        SetShipFcode(shipId, newFcode)
      )

      val after = turnInfo.stateAfterCommands

      after.ships(shipId).asInstanceOf[OwnShip].fcode should be (newFcode)
      turnInfo.commands should have size 1
    }

    "annihilate commands if sets fcode as it was initially" in {
      val gs = gameState
      val shipId = ShipId(1)
      val turnInfo = TurnInfo(gs.specs, gs.rst).withCommands(
        SetShipFcode(shipId, Fcode("mkt")),
        SetShipFcode(shipId, Fcode("md1"))
      )

      val after = turnInfo.stateAfterCommands

      after.ships(shipId).asInstanceOf[OwnShip].fcode should be (Fcode("md1"))
      turnInfo.commands shouldBe empty
    }
  }

  "start ship construction command" should {
    val gs = gameState
    val planetId = PlanetId(303)
    val cmd = StartShipConstruction(
      planetId, HullId(33), EngineId(9), BeamId(8), 6, LauncherId(1), 4
    )
    val turnInfo = TurnInfo(gs.specs, gs.rst).withCommands(cmd)

    val after = turnInfo.stateAfterCommands

    "set build order on starbase" in {
      after.bases(planetId).shipBeingBuilt should not be empty
      val order = after.bases(planetId).shipBeingBuilt.get
      order.hull.id should be (HullId(33))
      order.launchers.get.count should be (4)
      order.beams.get.count should be (6)
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
      val ti = TurnInfo(gs.specs, gs.rst).withCommands(cmd)

      val after = ti.stateAfterCommands

      "should leave empty build order " in {
        gs.rst.bases(planetId).shipBeingBuilt should not be empty
        after.bases(planetId).shipBeingBuilt shouldBe empty
      }

      "should NOT return resources to the planet" in {
        after.planets(planetId).surfaceMinerals shouldEqual gs.rst.planets(planetId).surfaceMinerals
      }
    }

    "the start build ship command was issued by player" should {
      val gs = gameState
      val planetId = PlanetId(186)
      //Build SDSF with stardrive 1
      val cmd1 = StartShipConstruction(planetId, HullId(15), EngineId(1), BeamId(0), 0, LauncherId(0), 0)
      val cmd2 = StopShipConstruction(planetId)
      val ti = TurnInfo(gs.specs, gs.rst).withCommands(cmd1, cmd2)

      val after = ti.stateAfterCommands

      "build order should be empty" in {
        after.bases(planetId).shipBeingBuilt shouldBe empty
      }

      "return resources subtracted" in {
        after.planets(planetId).surfaceMinerals shouldEqual gs.rst.planets(planetId).surfaceMinerals
        after.planets(planetId).money shouldEqual gs.rst.planets(planetId).money
        after.planets(planetId).supplies shouldEqual gs.rst.planets(planetId).supplies
      }

      "commands should annihilate each other" in {
        ti.commands shouldBe empty
      }
    }
  }
}
