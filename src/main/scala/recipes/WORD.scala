package replanets.recipes

import java.nio.{ByteBuffer, ByteOrder}

object WORD extends BinaryReadRecipe[Short] {

  import IteratorExtensions._

  val size = 2

  def read(source: Iterator[Byte]): Short = {
    ByteBuffer
      .wrap(source.readSome(size).toArray)
      .order(ByteOrder.LITTLE_ENDIAN)
      .getShort
  }
}
