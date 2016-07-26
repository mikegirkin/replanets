package replanets.recipes

/**
  * Created by mgirkin on 26/07/2016.
  */
class ArrayRecipe[T](length: Int)(implicit elementRecipe: BinaryReadRecipe[T]) extends BinaryReadRecipe[IndexedSeq[T]] {

  override val size: Int = elementRecipe.size * length

  override def read(source: Iterator[Byte]): IndexedSeq[T] = {
    (0 until length).map ( _ =>
      elementRecipe.read(source)
    )
  }

}


