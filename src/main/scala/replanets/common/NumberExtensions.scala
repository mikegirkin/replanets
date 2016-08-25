package replanets.common

/**
  * Created by mgirkin on 14/08/2016.
  */
object NumberExtensions {
  implicit class NumberExt[T : Numeric](val it: T) {
    def between(low: T, hi: T) =
      if(implicitly[Numeric[T]].lteq(low, it) && implicitly[Numeric[T]].lteq(it, hi)) true else false
  }

  implicit class IntExt(val it: Int) {
    def lowerBound(limit: Int): Int = {
      if(it < limit) limit else it
    }

    def upperBound(limit: Int): Int = {
      if(it > limit) limit else it
    }

    def bounded(low: Int, hi: Int): Int = {
      if(it < low) low
      else if(it > hi) hi
      else it
    }
  }
}
