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

      base.storedHulls(HullId(15)) should be (1) //SDSF
      base.storedHulls(HullId(34)) should be (1) //D7a painmaker
      base.storedHulls(HullId(35)) should be (1) //Victorious
    }
  }

  "Can read dos-style RST" in {
    val specs = Specs.fromDirectory(Paths.get("./testfiles/testgame-10/"))
    val path = Paths.get("./testfiles/testgame-10/db/1/player10.rst")
    val rst = RstFileReader.read(path, RaceId(10), specs)

    rst.generalInfo.turnNumber should be(1)
  }
}
