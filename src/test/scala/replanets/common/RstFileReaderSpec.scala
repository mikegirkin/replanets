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

      hulls should contain theSameElementsAs Seq(
        "NEUTRONIC FUEL CARRIER",
        "SMALL DEEP SPACE FREIGHTER",
        "MEDIUM DEEP SPACE FREIGHTER",
        "LARGE DEEP SPACE FREIGHTER",
        "SUPER TRANSPORT FREIGHTER",
        "VALIANT WIND CLASS CARRIER",
        "DETH SPECULA CLASS FRIGATE",
        "D7a PAINMAKER CLASS CRUISER",
        "VICTORIOUS CLASS BATTLESHIP",
        "D7 COLDPAIN CLASS CRUISER",
        "ILL WIND CLASS BATTLECRUISER",
        "D3 THORN CLASS DESTROYER",
        "D19b NEFARIOUS CLASS DESTROYER",
        "LITTLE PEST CLASS ESCORT",
        "SABER CLASS FRIGATE",
        "SMALL TRANSPORT",
        "NEUTRONIC REFINERY SHIP",
        "MERLIN CLASS ALCHEMY SHIP"
      )

      base.storedHulls(HullId(14)) should be (1)
      base.storedHulls(HullId(15)) should be (1)
      base.storedHulls(HullId(104)) should be (1)
    }
  }
}
