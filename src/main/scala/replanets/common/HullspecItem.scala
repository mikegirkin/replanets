package replanets.common

import java.nio.file.{Files, Path}

import replanets.recipes.{RecordRecipe, SpacePaddedString, WORD}

case class HullspecItem(
  id: Int,
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

  def read(idx: Int, it: Iterator[Byte]) = {
    HullspecItem(
      idx,
      SpacePaddedString(nameLength).read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it)
    )
  }

  def fromFile(file: Path): IndexedSeq[HullspecItem] = {
    val it = Files.readAllBytes(file).iterator
    (1 to Constants.HullsInHullspec).map { idx =>
      read(idx, it)
    }
  }
}
