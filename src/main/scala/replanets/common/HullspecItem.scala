package replanets.common

import replanets.recipes.{RecordRecipe, SpacePaddedString, WORD}

case class HullspecItem(
  name: String,
  pictureNumber: Short,
  damagedShipPictureNumber: Short,
  triCost: Short,
  durCost: Short,
  molCost: Short,
  fuelTankSize: Short,
  crewSize: Short,
  enginesNumber: Short,
  mass: Short,
  techLevel: Short,
  cargo: Short,
  fighterBaysNumber: Short,
  maxTorpedoLaunchers: Short,
  maxBeamWeapons: Short,
  moneyCost: Short
)

object HullspecItem {

  val nameLength = 30

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
    WORD,
    WORD,
    WORD,
    WORD,
    WORD,
    WORD,
    WORD
  )(HullspecItem.apply)

  def readFromFile(filename: String): IndexedSeq[HullspecItem] =
    recipe
      .readFromFile(filename)
      .toIndexedSeq
}
