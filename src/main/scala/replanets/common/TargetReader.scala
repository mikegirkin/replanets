package replanets.common

import replanets.recipes.{DWORD, SpacePaddedString, WORD}

/**
  * Created by mgirkin on 26/07/2016.
  */

case class TargetRecord(
  shipId: Short,
  owner: Short,
  warp: Short,
  x: Short,
  y: Short,
  hullType: Short,
  heading: Short,
  name: String
) extends ObjectWithCoords


object TargetReader {
  private def readTargetRecord(it: Iterator[Byte]): TargetRecord = {
    TargetRecord(
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      SpacePaddedString(20).read(it)
    )
  }

  private def readDosRecords(rst: Array[Byte]): IndexedSeq[TargetRecord] = {
    val pointer = DWORD.read(rst.slice(4, 8).iterator)
    val it = rst.iterator.drop(pointer - 1)
    val recordNumber = WORD.read(it)
    for(i <- 0 until recordNumber) yield readTargetRecord(it)
  }

  private def readWinplanRecords(rst: Array[Byte]): IndexedSeq[TargetRecord] = {
    val winplanPointer = DWORD.read(rst.iterator.slice(40, 44))
    val it = rst.iterator.drop(winplanPointer - 1).drop(500 * 8 + 600 + 50 * 4 + 682 + 7800)
    val signature = SpacePaddedString(4).read(it)
    assert(signature == "1211" || signature == "1120")
    val numberOfRecords = DWORD.read(it)
    (0 until numberOfRecords).map { idx => readTargetRecord(it) }
  }

  def readFromRst(rst: Array[Byte], isWinplan: Boolean): IndexedSeq[TargetRecord] = {
    readDosRecords(rst) ++ (if(isWinplan) readWinplanRecords(rst) else Seq())
  }
}
