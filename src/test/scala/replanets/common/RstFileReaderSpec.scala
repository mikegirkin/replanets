package replanets.common

import java.nio.file.Paths

import org.scalatest.{Matchers, WordSpec}
import replanets.model.Specs

class RstFileReaderSpec extends WordSpec with Matchers {
  "Reading bases" should {
    "Correctly read stored hulls" in {

      val specs = Specs.fromDirectory(Paths.get("./testfiles/testgame-4/"))
      val path = Paths.get("./testfiles/testgame-4/db/38/player4.rst")
      val rst = RstFileReader.read(path, RaceId(4), specs)

      val base = rst.bases(PlanetId(16))
      val klingonHulls = specs.getRaceHulls(RaceId(4))

      val hulls = klingonHulls.map {
        _.name
      }
      hulls(0) should be("NEUTRONIC FUEL CARRIER")
      hulls(1) should be("SMALL DEEP SPACE FREIGHTER")
      hulls(2) should be("MEDIUM DEEP SPACE FREIGHTER")
      hulls(3) should be("LARGE DEEP SPACE FREIGHTER")
      hulls(4) should be("SUPER TRANSPORT FREIGHTER")
      hulls(5) should be("VALIANT WIND CLASS CARRIER")
      hulls(6) should be("DETH SPECULA CLASS FRIGATE")
      hulls(7) should be("D7a PAINMAKER CLASS CRUISER")
      hulls(8) should be("VICTORIOUS CLASS BATTLESHIP")
      hulls(9) should be("D7 COLDPAIN CLASS CRUISER")
      hulls(10) should be("ILL WIND CLASS BATTLECRUISER")
      hulls(11) should be("D3 THORN CLASS DESTROYER")
      hulls(12) should be("D19b NEFARIOUS CLASS DESTROYER")
      hulls(13) should be("LITTLE PEST CLASS ESCORT")
      hulls(14) should be("SABER CLASS FRIGATE")
      hulls(15) should be("SMALL TRANSPORT")
      hulls(16) should be("NEUTRONIC REFINERY SHIP")
      hulls(17) should be("MERLIN CLASS ALCHEMY SHIP")

      base.storedHulls(HullId(14)) should be (1)
      base.storedHulls(HullId(15)) should be (1)
      base.storedHulls(HullId(104)) should be (1)
    }
  }
}
