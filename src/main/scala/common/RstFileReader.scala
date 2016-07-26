package replanets.common

import java.nio.file.{Files, Path}

import replanets.recipes.{DWORD, SpacePaddedString}

case class RstFile(
  pointers: IndexedSeq[Int],
  signature: String,
  subversion: String,
  winplanDataPosition: Int,
  leechPosition: Int,

  ships: IndexedSeq[ShipRecord]
)

object RstFileReader {

  def read(file: Path) = {
    val buffer = Files.readAllBytes(file)
    val it = buffer.iterator

    val pointers = DWORD.readSome(it, 8)
    val signature = SpacePaddedString(6).read(it)
    val subversion = SpacePaddedString(2).read(it)
    val winplanDataPosition = DWORD.read(it)
    val leechPosition = DWORD.read(it)

    val ships = ShipsReader.read(buffer.iterator.drop(pointers(0) + 1))

    RstFile(pointers, signature, subversion, winplanDataPosition, leechPosition, ships)
  }

}