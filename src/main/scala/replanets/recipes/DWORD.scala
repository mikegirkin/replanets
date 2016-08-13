package replanets.recipes

import java.nio.{ByteBuffer, ByteOrder}

import replanets.common.IteratorExtensions

object DWORD extends BinaryReadRecipe[Int] {

  import IteratorExtensions._

  val size = 4

  def read(source: Iterator[Byte]): Int =
    ByteBuffer.wrap(source.readSome(size).toArray)
      .order(ByteOrder.LITTLE_ENDIAN)
      .getInt
}