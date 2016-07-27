package replanets.recipes

import java.nio.file.Paths

trait BinaryReadRecipe[T] {

  val size: Int
  def read(source: Iterator[Byte]): T

  def readSome(source: Iterator[Byte], amount: Int): IndexedSeq[T] =
    (0 until amount).map(_ => read(source))

  def readAll(bytes: Iterator[Byte]): IndexedSeq[T] = {
    bytes.grouped(size).map { r =>
      read(r.iterator)
    }.toIndexedSeq
  }

  def readFromFile(filename: String): IndexedSeq[T] = {
    readAll(
      java.nio.file.Files.readAllBytes(Paths.get(filename)).iterator
    )
  }

  def readFromFile(filename: String, exactAmount: Int): IndexedSeq[T] = {
    readAll(
      java.nio.file.Files.readAllBytes(Paths.get(filename))
        .take(exactAmount*size)
        .iterator
    )
  }
}
