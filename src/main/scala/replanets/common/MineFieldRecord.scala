package replanets.common

import replanets.recipes.WORD

case class MineFieldRecord(
  x: Short,
  y: Short,
  radius: Short,
  owner: Short
)

object MineFieldsSectionReader {
  def read(it: Iterator[Byte]): Seq[MineFieldRecord] = {
    (1 to 500).map { idx =>
      MineFieldRecord(
        WORD.read(it),
        WORD.read(it),
        WORD.read(it),
        WORD.read(it)
      )
    }
  }
}
