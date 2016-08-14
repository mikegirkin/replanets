package replanets.common

/**
  * Created by mgirkin on 14/08/2016.
  */
object ByteExtensions {
  implicit class ByteExt(val it: Byte) extends AnyVal {
    def toUnsignedInt: Int =
      it.toInt & 0x000000FF
  }
}
