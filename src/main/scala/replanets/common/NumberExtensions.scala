package replanets.common

/**
  * Created by mgirkin on 14/08/2016.
  */
object NumberExtensions {
  implicit class NumberExt[T : Numeric](val it: T) {
    def between(low: T, hi: T) =
      if(implicitly[Numeric[T]].lteq(low, it) && implicitly[Numeric[T]].lteq(it, hi)) true else false
  }
}
