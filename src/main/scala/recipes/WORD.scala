package replanets.recipes

import java.nio.{ByteBuffer, ByteOrder}

object WORD extends BinaryReadRecipe[Short] {
  val size = 2

  def read(source: Iterator[Byte]): Short = {
    ByteBuffer
      .wrap(source.take(size).toArray)
      .order(ByteOrder.LITTLE_ENDIAN)
      .getShort
  }
}
