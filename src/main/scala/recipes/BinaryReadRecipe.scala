package replanets.recipes

import java.nio.file.Paths

trait BinaryReadRecipe[T] {

  val size: Int
  def read(source: Iterator[Byte]): T

  def readAll(bytes: Iterator[Byte]): Seq[T] = {

    bytes.grouped(size).map { r =>
      read(r.iterator)
    }.toSeq
  }

  def readFromFile(filename: String): Seq[T] = {
    readAll(
      java.nio.file.Files.readAllBytes(Paths.get(filename)).iterator
    )
  }

  def readFromFile(filename: String, exactAmount: Int): Seq[T] = {
    readAll(
      java.nio.file.Files.readAllBytes(Paths.get(filename))
        .take(exactAmount*size)
        .iterator
    )
  }
}
