package replanets.common

import java.nio.file.Paths

import org.scalatest.{Matchers, WordSpec}
import replanets.model.{Specs, THostFormulas}

class StaticFilesReadSpec extends WordSpec with Matchers {

  private def getResourceFilename(path: String) = getClass.getResource(path).getPath

  "A torpspec.dat reader reads file correctly" in {

    val file = Paths.get("./testfiles/THost/torpspec.dat")

    val torps = TorpspecItem.fromFile(file)

    torps should have size Constants.TorpspecRecordsNumber
    torps(5).name should be("Mark 4 Photon")
    torps(5).kill should be(13)
    torps(5).launcherCost.money should be (20)
    torps(5).id should be (LauncherId(6))
  }

  "A hullspec.dat should be read correctly" in {

    val file = Paths.get("./testfiles/THost/hullspec.dat")

    val hulls = HullspecItem.fromFile(file, Constants.thostHullFunctions)

    hulls should have size 105
    val hull = hulls(65)
    hull.name should be ("OPAL CLASS TORPEDO BOAT")
    hull.techLevel should be (2)
    hull.maxBeamWeapons should be (1)
    hull.maxTorpedoLaunchers should be (1)
    hull.enginesNumber should be (1)
    hull.cost.money should be (60)
    hull.cost.dur should be (12)
    hull.cost.tri should be (29)
    hull.cost.mol should be (20)
    hull.mass should be (67)
    hull.cargo should be (19)
    hull.fuelTankSize should be (55)
    hull.crewSize should be (25)

    val meteor = hulls.find(_.id == HullId(46)).get
    meteor.specials should contain theSameElementsAs Set(Cloak, Gravitonic)
    meteor.name should be ("METEOR CLASS BLOCKADE RUNNER")

  }

  "truehull.dat should be read correctly" in {

    val file = Paths.get("./testfiles/THost/truehull.dat")

    val assignment = HullAssignment.fromFile(file)

    assignment.availableHulls should have size 11
    assignment.availableHulls(0)(0) should be (1)
    assignment.availableHulls(10).last should be (105)
  }


  "engspec reader should read correctly" in {
    val file = Paths.get("./testfiles/THost/engspec.dat")

    val engines = EngspecItem.fromFile(file)

    engines(8).id should be (EngineId(9))
    engines(8).techLevel should be (10)
    engines(8).name should be ("Transwarp Drive")
    engines(8).cost.money should be (300)
  }

  "beamspec reader should read data correclty" in {
    val file = Paths.get("./testfiles/THost/beamspec.dat")

    val beams = BeamspecItem.fromFile(file)

    beams(9).id should be (BeamId(10))
    beams(9).techLevel should be (10)
    beams(9).name should be ("Heavy Phaser")
    beams(9).cost.money should be (54)
  }

  "hull assignments should work correctly" in {
    val dir = Paths.get("./testfiles/testgame-4")
    val specs = Specs.fromDirectory(dir)(THostFormulas, new Missions(RaceId(4), THost))

    val vic = specs.getRaceHulls(RaceId(4))(16)
    vic.name should be ("VICTORIOUS CLASS BATTLESHIP")
  }

}
