package replanets.recipes

/**
  * Created by mgirkin on 28/07/2016.
  */
object BYTE extends BinaryReadRecipe[Byte] {
  def read(source: Iterator[Byte]): Byte = {
    source.next()
  }
}
