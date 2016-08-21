package replanets.common

import org.scalatest.{Matchers, WordSpec}

class THostFormulasSpec extends WordSpec with Matchers {
  import replanets.model.THostFormulas._

  "erndDiv implemented correctly" in {
    erndDiv(5, 5) should be(1)
    erndDiv(10, 3) should be(3)
    erndDiv(11, 3) should be(4)
    erndDiv(15, 2) should be(8)
    erndDiv(13, 2) should be(6)
  }

  "correctly calculates tech level upgrade costs" in {

    techUpgradeCost(1, 10) should be (4500)
    techUpgradeCost(9, 10) should be (900)
    techUpgradeCost(8, 10) should be (1700)
    techUpgradeCost(10, 4) should be (0)
  }
}

