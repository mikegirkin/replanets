package replanets.common

import org.scalatest.{Matchers, WordSpec}

/**
  * Created by mgirkin on 26/07/2016.
  */
class IteratorExtesionTest extends WordSpec with Matchers {

  import replanets.recipes.IteratorExtensions._

  "readSome" should {
    "read 4 items" in {
      val arr = Array(10, 34, 98, 8723, 43, 94)
      val it = arr.iterator

      val result = it.readSome(4).toArray

      assert(it.next() == arr(4))
      result should contain theSameElementsInOrderAs arr.take(4)
    }
  }
}
