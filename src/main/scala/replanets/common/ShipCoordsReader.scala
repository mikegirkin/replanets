package replanets.common

import replanets.recipes.WORD

case class ShipCoordsRecord(
  id: Int,
  x: Short,
  y: Short,
  owner: Short,
  mass: Short
) extends ObjectWithCoords

object ShipCoordsReader {
  def read(it: Iterator[Byte]): IndexedSeq[ShipCoordsRecord] = {
    (1 to 500).map { idx =>
      ShipCoordsRecord(
        idx,
        WORD.read(it),
        WORD.read(it),
        WORD.read(it),
        WORD.read(it)
      )
    }.filter { scr => scr.x != 0 && scr.y != 0 }
  }
}
