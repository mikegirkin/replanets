package replanets.common

import org.scalatest.{Matchers, WordSpec}

/**
  * Created by mgirkin on 05/08/2016.
  */
class IonStormSpec extends WordSpec with Matchers {
  "Should properly calculate storm category" in {
    val storms = Seq(
      IonStorm(1, 1, 1, 10, 20, 9, 23),
      IonStorm(2, 1, 2, 10, 67, 8, 213),
      IonStorm(3, 1, 3, 10, 205, 7, 123)
    )

    storms(0).category should be(1)
    storms(1).category should be(2)
    storms(2).category should be(5)
  }

}
