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

  "maxFactories for given money" should {
    "be correct" in {
      f.maxFactoriesForMoney(4, 0) shouldBe 1
      f.maxFactoriesForMoney(1, 3) shouldBe 1
      f.maxFactoriesForMoney(0, 10) shouldBe 0
      f.maxFactoriesForMoney(25, 100) shouldBe 25
      f.maxFactoriesForMoney(25, 50) shouldBe (16 + 1 + 1)
    }
  }

  "maxMinesForMoney" should {
    "be correct" in {
      f.maxMinesForMoney(5, 0) shouldBe 1
      f.maxMinesForMoney(1, 4) shouldBe 1
      f.maxMinesForMoney(0, 123) shouldBe 0
      f.maxMinesForMoney(20, 101) shouldBe 20
      f.maxMinesForMoney(20, 42) shouldBe (10 + 1 + 1)
    }
  }
}

