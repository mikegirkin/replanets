package replanets.common

import org.scalatest.{Matchers, WordSpec}

/**
  * Created by mgirkin on 06/08/2016.
  */
class THostFormulasSpec extends WordSpec with Matchers {
  import replanets.model.THostFormulas._

  "erndDiv implemented correctly" in {
    erndDiv(5, 5) should be(1)
    erndDiv(10, 3) should be(3)
    erndDiv(11, 3) should be(4)
    erndDiv(15, 2) should be(8)
    erndDiv(13, 2) should be(6)
  }
}