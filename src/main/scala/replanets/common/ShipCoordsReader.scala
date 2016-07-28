package replanets.common

import replanets.recipes.{ArrayRecipe, RecordRecipe, WORD}

case class ShipCoordsRecord(
  id: Int,
  x: Short,
  y: Short,
  owner: Short,
  mass: Short
)

object ShipCoordsReader {
  def read(it: Iterator[Byte]): IndexedSeq[ShipCoordsRecord] = {
    ArrayRecipe(500, RecordRecipe(WORD, WORD, WORD, WORD){ case (x, y, owner, mass) => (x, y, owner, mass)})
      .read(it)
      .filter { case (x, y, owner, mass) => x!=0 && y!=0 }
      .zipWithIndex
      .map { case ((x, y, owner, mass), idx) => ShipCoordsRecord(idx + 1, x, y, owner, mass) }
  }
}
