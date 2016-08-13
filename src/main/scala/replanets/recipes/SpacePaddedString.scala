package replanets.recipes

import java.nio.ByteBuffer
import java.nio.charset.Charset

import replanets.common.IteratorExtensions

object SpacePaddedString {
  def apply(length: Int) = new SpacePaddedString(length)
}

class SpacePaddedString(length: Int) extends BinaryReadRecipe[String] {

  import IteratorExtensions._

  final val Decoder = Charset.forName("ASCII")

  override def read(source: Iterator[Byte]): String = {
    val bytes = source.readSome(length).toArray
    Decoder.decode(ByteBuffer.wrap(bytes)).toString.trim
  }
}