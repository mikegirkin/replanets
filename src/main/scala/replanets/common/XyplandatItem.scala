package replanets.common

import replanets.recipes.{RecordRecipe, WORD}


case class XyplandatItem(
  x: Short,
  y: Short,
  owner: Short
)

object XyplandatItem {
  val recipe = RecordRecipe(WORD, WORD, WORD)(XyplandatItem.apply)
}
