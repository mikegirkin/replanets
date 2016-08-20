package replanets.common

import java.nio.file.{Files, Path}

import replanets.recipes.{WORD, _}

case class EngspecItem(
  id: OneBasedIndex,
  name: String,
  cost: Cost,
  techLevel: Short,
  fuel: IndexedSeq[Int]
)

object EngspecItem {
  val engineNameLength = 20

  def fromFile(file: Path) = {
    val it = Files.readAllBytes(file).iterator
    (1 to Constants.EnginesInSpec).map { idx =>
      EngspecItem(
        OneBasedIndex(idx),
        SpacePaddedString(engineNameLength).read(it),
        Cost(
          money = WORD.read(it),
          tri = WORD.read(it),
          dur = WORD.read(it),
          mol = WORD.read(it)
        ),
        WORD.read(it),
        ArrayRecipe(9, DWORD).read(it)
      )
    }
  }
}


