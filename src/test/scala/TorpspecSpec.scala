package replanets.tests

import common.Constants
import org.scalatest.{Matchers, WordSpec}
import replanets.common.TorpspecItem

class TorpspecSpec extends WordSpec with Matchers {

  "A torpspec reader reads file correctly" in {

    val filename = getClass.getResource("/torpspec.dat").getPath

    val torps = TorpspecItem.readFromFile(filename)

    torps should have size Constants.TorpspecRecordsNumber
    torps(5).name should be ("Mark 4 Photon")
    torps(5).kill should be (13)
  }

}
