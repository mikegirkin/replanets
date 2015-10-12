package replanets.common

import replanets.recipes.{WORD, RecordRecipe}


case class XyplandatItem(
  x: Short,
  y: Short,
  owner: Short
)

object XyplandatItem {
  val recipe = RecordRecipe(WORD, WORD, WORD)(XyplandatItem.apply)
}
