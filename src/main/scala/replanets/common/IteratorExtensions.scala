package replanets.common

import replanets.recipes.BinaryReadRecipe

/**
  * Created by mgirkin on 26/07/2016.
  */
object IteratorExtensions {

  implicit class IteratorExt[T](val it: Iterator[T]) extends AnyVal {

    def readSome(length: Int): Iterator[T] = {
      (for(i <- 0 until length) yield if(it.hasNext) Some(it.next()) else None
        ).flatten.iterator
    }

  }

  implicit class ByteIteratorExt(val it: Iterator[Byte]) extends AnyVal {
    def read[T](recipe: BinaryReadRecipe[T]): T = {
      recipe.read(it)
    }

    def read[T](recipe:BinaryReadRecipe[T], amount: Int): IndexedSeq[T] = {
      recipe.readSome(it, amount)
    }
  }
}

object ByteExtensions {
  implicit class ByteExt(val it: Byte) extends AnyVal {
    def toUnsignedInt: Int =
      it.toInt & 0x000000FF
  }
}

object NumberExtensions {
  implicit class NumberExt[T : Numeric](val it: T) {
    def between(low: T, hi: T) =
      if(implicitly[Numeric[T]].lteq(low, it) && implicitly[Numeric[T]].lteq(it, hi)) true else false
  }
}
