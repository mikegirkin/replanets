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
  cost: Cost,
  specials: Set[HullFunc]
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
    def toHullspecItem(specials: Set[HullFunc]) = HullspecItem(
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
      cost = Cost(triCost, durCost, molCost, moneyCost),
      specials
    )
  }

  val nameLength = 30

  private def read(idx: Int, it: Iterator[Byte]) = {
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
    )
  }

  def fromFile(file: Path, specials: Map[HullId, Set[HullFunc]]): IndexedSeq[HullspecItem] = {
    val it = Files.readAllBytes(file).iterator
    (1 to Constants.HullsInHullspec).map { idx =>
      read(idx, it).toHullspecItem(specials.getOrElse(HullId(idx), Set()))
    }
  }
}
