package replanets.recipes

import java.nio.charset.Charset
import java.nio.{ByteOrder, ByteBuffer}

trait BinaryReadRecipe[T] {
  def read(source: Iterator[Byte]): T
  val size: Int
}

object WORD extends BinaryReadRecipe[Short] {
  val size = 2

  def read(source: Iterator[Byte]): Short =
    ByteBuffer.wrap(Array(source.next(), source.next())).order(ByteOrder.LITTLE_ENDIAN).getShort
}

class SpacePaddedString(length: Int) extends BinaryReadRecipe[String] {
  final val Decoder = Charset.forName("ASCII")

  override val size: Int = length

  override def read(source: Iterator[Byte]): String = {
    val bytes = Range(0, length).map { _ => source.next() }.toArray
    Decoder.decode(ByteBuffer.wrap(bytes)).toString.trim
  }
}

object SpacePaddedString {
  def apply(length: Int) = new SpacePaddedString(length)
}

object RecordRecipe {
  def apply[R, A1, A2, A3](a1: BinaryReadRecipe[A1], a2: BinaryReadRecipe[A2], a3: BinaryReadRecipe[A3])(apply: (A1, A2, A3) => R) =
    new BinaryReadRecipe[R] {
      val size = a1.size + a2.size + a3.size

      override def read(source: Iterator[Byte]): R =
        apply(
          a1.read(source),
          a2.read(source),
          a3.read(source)
        )
    }

  def apply[R, A1](a1: BinaryReadRecipe[A1])(apply: (A1) => R) =
    new BinaryReadRecipe[R] {
      val size = a1.size

      override def read(source: Iterator[Byte]): R =
        apply(
          a1.read(source)
        )
    }
}