package replanets.common

import java.nio.file.{Files, Path}

import replanets.recipes.{RecordRecipe, SpacePaddedString, WORD}

case class HullspecItem(
  id: HullId,
  name: String,
  pictureNumber: Short,
  damagedShipPictureNumber: Short,
  fuelTankSize: Short,
  crewSize: Short,
  enginesNumber: Short,
  mass: Short,
  techLevel: Short,
  cargo: Short,
  fighterBaysNumber: Short,
  maxTorpedoLaunchers: Short,
  maxBeamWeapons: Short,
  cost: Cost
)

object HullspecItem {

  private case class HullspecRecord(
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
  ) {
    def toHullspecItem = HullspecItem(
      HullId(id),
      name,
      pictureNumber,
      damagedShipPictureNumber,
      fuelTankSize,
      crewSize,
      enginesNumber,
      mass,
      techLevel,
      cargo,
      fighterBaysNumber,
      maxTorpedoLaunchers,
      maxBeamWeapons,
      cost = Cost(triCost, durCost, molCost, moneyCost)
    )
  }

  val nameLength = 30

  def read(idx: Int, it: Iterator[Byte]) = {
    HullspecRecord(
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
    ).toHullspecItem
  }

  def fromFile(file: Path): IndexedSeq[HullspecItem] = {
    val it = Files.readAllBytes(file).iterator
    (1 to Constants.HullsInHullspec).map { idx =>
      read(idx, it)
    }
  }
}
