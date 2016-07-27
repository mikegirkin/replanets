package replanets.common

import replanets.recipes.{SpacePaddedString, WORD}

/**
  * Created by mgirkin on 26/07/2016.
  */

case class TargetRecord(
  shipId: Short,
  owner: Short,
  warp: Short,
  xPosition: Short,
  yPosition: Short,
  hullType: Short,
  heading: Short,
  name: String
)


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

  def read(it: Iterator[Byte]): IndexedSeq[TargetRecord] = {
    val recordNumber = WORD.read(it)
    for(i <- 0 until recordNumber) yield readTargetRecord(it)
  }
}
