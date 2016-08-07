package replanets.common

import java.nio.file.Path

import replanets.recipes._

case class EngspecItem(
  name: String,
  moneyCost: Short,
  triCost: Short,
  durCost: Short,
  molCost: Short,
  techLevel: Short,
  fuel: IndexedSeq[Int]
)

object EngspecItem {
  val engineNameLength = 20

  val recipe = RecordRecipe(
    SpacePaddedString(engineNameLength),
    WORD,
    WORD,
    WORD,
    WORD,
    WORD,
    ArrayRecipe(9, DWORD)
  )(EngspecItem.apply)

  def fromFile(file: Path) = {
    recipe.readFromFile(file, 9)
  }
}


