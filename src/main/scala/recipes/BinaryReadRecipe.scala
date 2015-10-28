package replanets.recipes

import java.nio.file.Paths

trait BinaryReadRecipe[T] {

  val size: Int
  def read(source: Iterator[Byte]): T

  def readAll(bytes: Iterator[Byte], exactAmount: Option[Int] = None): Seq[T] = {
    def bound[A](it: Iterator[A]): Iterator[A] = exactAmount.map {
      x => it.take(x)
    }.getOrElse {
      it
    }

    bound(bytes.grouped(size)).map { r =>
      read(r.iterator)
    }.toSeq
  }

  def readFromFile(filename: String, exactAmount: Option[Int] = None): Seq[T] = {
    readAll(
      java.nio.file.Files.readAllBytes(Paths.get(filename)).iterator,
      exactAmount)
  }
}
