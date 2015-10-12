package replanets.common

import replanets.recipes.{WORD, SpacePaddedString, RecordRecipe}

case class TorpspecItem(
  name: String,
  moneyCost: Short,
  triCost: Short,
  durCost: Short,
  molCost: Short,
  mass: Short,
  techLevel: Short,
  kill: Short,
  damage: Short
)

object TorpspecItem {
  val recipe = RecordRecipe(
    SpacePaddedString(20),
    WORD,
    WORD,
    WORD,
    WORD,
    WORD,
    WORD,
    WORD,
    WORD
  )(TorpspecItem.apply)
}
