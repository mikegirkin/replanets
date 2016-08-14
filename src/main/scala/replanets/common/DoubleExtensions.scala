package replanets.common

/**
  * Created by mgirkin on 14/08/2016.
  */
object DoubleExtensions {
  implicit class ByteExt(val it: Double) extends AnyVal {
    def almostEqual(y: Double, precision: Double) = {
      if ((it - y).abs < precision) true else false
    }
  }
}
