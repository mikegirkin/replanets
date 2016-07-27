package replanets.common

import replanets.recipes.{DWORD, RecordRecipe, SpacePaddedString, WORD}

case class FuelConsumption(
  level1consumption: Int,
  level2consumption: Int,
  level3consumption: Int,
  level4consumption: Int,
  level5consumption: Int,
  level6consumption: Int,
  level7consumption: Int,
  level8consumption: Int,
  level9consumption: Int
)

object FuelConsumption {
  val recipe = RecordRecipe(
    DWORD,
    DWORD,
    DWORD,
    DWORD,
    DWORD,
    DWORD,
    DWORD,
    DWORD,
    DWORD
  )(FuelConsumption.apply)
}

case class EngspecItem(
  name: String,
  moneyCost: Short,
  triCost: Short,
  durCost: Short,
  molCost: Short,
  techLevel: Short,
  fuel: FuelConsumption
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
    FuelConsumption.recipe
  )(EngspecItem.apply)
}


