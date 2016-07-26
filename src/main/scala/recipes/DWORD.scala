package replanets.recipes

import java.nio.{ByteBuffer, ByteOrder}

object DWORD extends BinaryReadRecipe[Int] {

  import IteratorExtensions._

  val size = 4

  def read(source: Iterator[Byte]): Int =
    ByteBuffer.wrap(source.readSome(size).toArray)
      .order(ByteOrder.LITTLE_ENDIAN)
      .getInt
}