package replanets.common

import replanets.recipes.{DWORD, WORD}

case class ExplosionRecord(
  id: Short,
  x: Short,
  y: Short
) extends ObjectWithCoords

object ExplosionsReader {
  val numberOfRecords = 50

  def read(rst: Array[Byte]): IndexedSeq[ExplosionRecord] = {
    val winplanPointer = DWORD.read(rst.iterator.slice(40, 44))
    val it = rst.iterator.drop(winplanPointer - 1).drop(500 * 8 + 600)
    val allRecords = (1 to 50).map { idx =>
      ExplosionRecord(
        idx.toShort,
        WORD.read(it),
        WORD.read(it)
      )
    }
    allRecords.filter(_.x != 0)
  }
}
