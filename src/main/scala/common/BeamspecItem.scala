package replanets.common

import replanets.recipes.{WORD, SpacePaddedString, RecordRecipe}

case class BeamspecItem(
  name: String,
  moneyCost: Short,
  triCost: Short,
  durCost: Short,
  molCost: Short,
  mass: Short,
  techLevel: Short,
  killValue: Short,
  damageValue: Short
)

object BeamspecItem {
  
  val beamNameLength = 20
  val recipe = RecordRecipe(
    SpacePaddedString(beamNameLength),
    WORD,
    WORD,
    WORD,
    WORD,
    WORD,
    WORD,
    WORD,
    WORD
  )(BeamspecItem.apply)
}
