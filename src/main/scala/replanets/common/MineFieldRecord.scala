package replanets.common

import replanets.recipes.WORD

case class MineFieldRecord(
  id: Short,
  x: Short,
  y: Short,
  radius: Short,
  owner: Short
) extends ObjectWithCoords

object MineFieldsSectionReader {
  def read(it: Iterator[Byte]): IndexedSeq[MineFieldRecord] = {
    (1 to 500).map{ idx =>
      MineFieldRecord(
        idx.toShort,
        WORD.read(it),
        WORD.read(it),
        WORD.read(it),
        WORD.read(it)
      )
    }.filter( x =>
      x.owner != 0
    )
  }
}
