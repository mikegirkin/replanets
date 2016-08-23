package replanets.common

import org.scalatest.{Matchers, WordSpec}
import replanets.model.PHostFormulas

class PHostFormulasSpec extends WordSpec with Matchers {

  val f = PHostFormulas

  "Native tax formula" should {
    "return 0 for amorphous" in {
      val income = f.nativeTaxIncome(
        RaceId(2),
        NativeRace.Amorphous,
        NativeGovernment.Anarchy,
        33568,
        7,
        80,
        120
      )

      income shouldBe 0
    }

    "return normal income for other race" in {
      val income = f.nativeTaxIncome(
        RaceId(2),
        NativeRace.Ghipsoldal,
        NativeGovernment.Monarchy,
        69713, //Natives
        14, //14%
        87,
        1191 //Colonists
      )

      income shouldBe 1171
    }

    "return twice more for insectoids" in {
      val income = f.nativeTaxIncome(
        RaceId(2),
        NativeRace.Insectoid,
        NativeGovernment.Monarchy,
        69713, //Natives
        14, //14%
        87,
        1191 //Colonists
      )

      income shouldBe 2342
    }

    "return twice more for feds" in {
      val income = f.nativeTaxIncome(
        RaceId(1),
        NativeRace.Ghipsoldal,
        NativeGovernment.Monarchy,
        69713, //Natives
        14, //14%
        87,
        1191 //Colonists
      )

      income shouldBe 2342
    }

    "income is limited by colonists" in {
      val income = f.nativeTaxIncome(
        RaceId(2),
        NativeRace.Reptilian,
        NativeGovernment.Monarchy,
        69713, //Natives
        14, //14%
        87,
        540 //Colonists
      )

      income shouldBe 540
    }
  }

  "Colonist tax formula" should {
    "calculate tax correctly" in {
      val f = PHostFormulas

      val income = f.colonistTaxIncome(
        RaceId(2),
        22998,
        6,
        85
      )

      income shouldBe 138
    }

    "double the income for feds" in {
      val f = PHostFormulas

      val income = f.colonistTaxIncome(
        RaceId(1),
        22998,
        6,
        85
      )

      income shouldBe 276
    }
  }

  "Native happiness change formula" should {
    "calculate change correctly" in {
      val change = f.nativeHappinessChange(
        NativeRace.Bovinoid,
        NativeGovernment.Representative,
        39113,
        75,
        142,
        13
      )

      change shouldBe -5
    }
  }

  "Colonists happiness change formula" should {
    "be correct" in {
      val change = f.colonistHappinessChange(
        RaceId(10),
        29442,
        50,
        200,
        200,
        12
      )

      change shouldBe -2
    }
  }
}
