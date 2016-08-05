package replanets.common

import java.nio.file.Paths

import org.scalatest.{Matchers, WordSpec}

class StaticFilesReadSpec extends WordSpec with Matchers {

  private def getResourceFilename(path: String) = getClass.getResource(path).getPath

  "A torpspec.dat reader reads file correctly" in {

    val filename = getResourceFilename("/torpspec.dat")

    val torps = TorpspecItem.fromFile(Paths.get(filename))

    torps should have size Constants.TorpspecRecordsNumber
    torps(5).name should be("Mark 4 Photon")
    torps(5).kill should be(13)

  }

  "A hullspec.dat should be read correctly" in {

    val filename = getResourceFilename("/hullspec.dat")

    val hulls = HullspecItem.fromFile(Paths.get(filename))

    hulls should have size 105
    val hull = hulls(65)
    hull.name should be ("OPAL CLASS TORPEDO BOAT")
    hull.techLevel should be (2)
    hull.maxBeamWeapons should be (1)
    hull.maxTorpedoLaunchers should be (1)
    hull.enginesNumber should be (1)
    hull.moneyCost should be (60)
    hull.durCost should be (12)
    hull.triCost should be (29)
    hull.molCost should be (20)
    hull.mass should be (67)
    hull.cargo should be (19)
    hull.fuelTankSize should be (55)
    hull.crewSize should be (25)

  }

  "truehull.dat should be read correctly" in {

    val filename = getResourceFilename("/truehull.dat")

    val assignment = HullAssignment.fromFile(Paths.get(filename))

    assignment.availableHulls should have size 11
    assignment.availableHulls(0)(0) should be (0)
    assignment.availableHulls(10).last should be (104)
  }

}
