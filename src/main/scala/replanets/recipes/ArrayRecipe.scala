package replanets.recipes

/**
  * Created by mgirkin on 26/07/2016.
  */
class ArrayRecipe[T](length: Int)(implicit elementRecipe: BinaryReadRecipe[T]) extends BinaryReadRecipe[IndexedSeq[T]] {

  override def read(source: Iterator[Byte]): IndexedSeq[T] = {
    (0 until length).map ( _ =>
      elementRecipe.read(source)
    )
  }

}

object ArrayRecipe {
  def apply[T](length: Int, elementRecipe: BinaryReadRecipe[T]): ArrayRecipe[T] = new ArrayRecipe(length)(elementRecipe)
}