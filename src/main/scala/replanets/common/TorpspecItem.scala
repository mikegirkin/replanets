package replanets.common

import replanets.recipes.{RecordRecipe, SpacePaddedString, WORD}

case class TorpspecItem(
  name: String,
  topredoMoneyCost: Short,
  launcherMoneyCost: Short,
  triCost: Short,
  durCost: Short,
  molCost: Short,
  mass: Short,
  techLevel: Short,
  kill: Short,
  damage: Short
)

object TorpspecItem {
  val nameLength = 20

  val recipe = RecordRecipe(
    SpacePaddedString(nameLength),
    WORD,
    WORD,
    WORD,
    WORD,
    WORD,
    WORD,
    WORD,
    WORD,
    WORD
  )(TorpspecItem.apply)

  def readFromFile(filename: String): IndexedSeq[TorpspecItem] =
    TorpspecItem.recipe
      .readFromFile(filename, Constants.TorpspecRecordsNumber)
      .toIndexedSeq
  }