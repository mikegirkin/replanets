package replanets.recipes

import java.nio.ByteBuffer
import java.nio.charset.Charset

object SpacePaddedString {
  def apply(length: Int) = new SpacePaddedString(length)
}

class SpacePaddedString(length: Int) extends BinaryReadRecipe[String] {
  final val Decoder = Charset.forName("ASCII")

  override val size: Int = length

  override def read(source: Iterator[Byte]): String = {
    val bytes = source.take(size).toArray
    Decoder.decode(ByteBuffer.wrap(bytes)).toString.trim
  }
}