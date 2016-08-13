package replanets.recipes

import java.nio.ByteBuffer
import java.nio.charset.Charset

import replanets.common.IteratorExtensions

/**
  * Created by mgirkin on 09/08/2016.
  */
object FixedLengthString {
  def apply(length: Int): FixedLengthString = new FixedLengthString(length)
}

class FixedLengthString(length: Int) extends BinaryReadRecipe[String] {
  import IteratorExtensions._

  final val Decoder = Charset.forName("ASCII")

  override def read(source: Iterator[Byte]): String = {
    val bytes = source.readSome(length).toArray
    Decoder.decode(ByteBuffer.wrap(bytes)).toString
  }
}
