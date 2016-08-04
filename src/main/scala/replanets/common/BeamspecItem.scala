package replanets.common

import java.nio.file.Path

import replanets.recipes.{RecordRecipe, SpacePaddedString, WORD}

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

  def fromFile(file: Path): IndexedSeq[BeamspecItem] = {
    recipe.readFromFile(file, Constants.BeamsInBeamspec)
  }
}
