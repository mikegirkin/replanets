package replanets.recipes

import java.nio.file.Path

trait BinaryReadRecipe[T] {

  val size: Int
  def read(source: Iterator[Byte]): T

  def readSome(source: Iterator[Byte], amount: Int): IndexedSeq[T] =
    (0 until amount).map(_ => read(source))

  def readSome(source: )

  def readAll(bytes: Iterator[Byte]): IndexedSeq[T] = {
    bytes.grouped(size).map { r =>
      read(r.iterator)
    }.toIndexedSeq
  }

  def readFromFile(file: Path): IndexedSeq[T] = {
    readAll(
      java.nio.file.Files.readAllBytes(file).iterator
    )
  }

  def readFromFile(file: Path, exactAmount: Int): IndexedSeq[T] = {
    readAll(
      java.nio.file.Files.readAllBytes(file)
        .take(exactAmount*size)
        .iterator
    )
  }
}