package replanets.recipes

/**
  * Created by mgirkin on 28/07/2016.
  */
object BYTE extends BinaryReadRecipe[Byte] {
  val size = 1

  def read(source: Iterator[Byte]): Byte = {
    source.next()
  }
}
