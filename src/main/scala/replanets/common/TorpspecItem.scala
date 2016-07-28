package replanets.common

import java.nio.file.Path

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

  def fromFile(file: Path): IndexedSeq[TorpspecItem] =
    recipe.readFromFile(file, Constants.TorpspecRecordsNumber)
}