package replanets.recipes

import java.nio.file.Path

trait BinaryReadRecipe[T] {

  def read(source: Iterator[Byte]): T

  def readSome(source: Iterator[Byte], amount: Int): IndexedSeq[T] =
    (0 until amount).map(_ => read(source))

  def readFromFile(file: Path, amount: Int): IndexedSeq[T] = {
    readSome(java.nio.file.Files.readAllBytes(file).iterator, amount)
  }
}