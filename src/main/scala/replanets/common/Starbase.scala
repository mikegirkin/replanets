package replanets.common

import replanets.model.THostFormulas

case class ShipCost(
  hullCost: Cost,
  enginesCost: Cost,
  beamsCost: Cost,
  launcherCost: Cost,
  techCost: Cost
) {
  def total: Cost = hullCost.add(enginesCost).add(beamsCost).add(launcherCost).add(techCost)
}

case class ShipBuildOrder(
  hull: HullspecItem,
  engine: EngspecItem,
  beam: BeamspecItem,
  beamCount: Int,
  launchers: TorpspecItem,
  launcherCount: Int
)

case class Starbase(
  id: PlanetId,
  planet: PlanetRecord,
  owner: RaceId,
  defences: Int,
  damage: Int,
  engineTech: Int,
  hullsTech: Int,
  beamTech: Int,
  torpedoTech: Int,
  storedEngines: IndexedSeq[Int],
  storedHulls: Map[HullId, Int],
  storedBeams: IndexedSeq[Int],
  storedLaunchers: IndexedSeq[Int],
  storedTorpedoes: IndexedSeq[Int],
  fightersNumber: Int,
  actionedShipId: Int,
  shipAction: Int,
  primaryOrder: Int,
  shipBeingBuilt: Option[ShipBuildOrder]
) {

  def shipCostAtStarbase(
    hullspec: HullspecItem,
    engspec: EngspecItem,
    beamspec: BeamspecItem,
    numberOfBeams: Int,
    torpspec: TorpspecItem,
    numberOfLaunchers: Int): ShipCost = {

    val hullCost = if(storedHulls.getOrElse(hullspec.id, 0) != 0) Cost.zero else hullspec.cost
    val enginesToBuild = lowerLimit(hullspec.enginesNumber - storedEngines(engspec.id.value - 1), 0)
    val enginesCost = engspec.cost.mul(enginesToBuild)
    val beamsToBuild = lowerLimit(numberOfBeams - storedBeams(beamspec.id.value - 1), 0)
    val beamsCost = beamspec.cost.mul(beamsToBuild)
    val launchersToBuild = lowerLimit(numberOfLaunchers - storedLaunchers(torpspec.id.value - 1), 0)
    val launcherCost = torpspec.launcherCost.mul(launchersToBuild)

    val techCost =
      THostFormulas.techUpgradeCost(hullsTech, hullspec.techLevel) +
      THostFormulas.techUpgradeCost(engineTech, engspec.techLevel) +
      THostFormulas.techUpgradeCost(beamTech, beamspec.techLevel) +
      THostFormulas.techUpgradeCost(torpedoTech, torpspec.techLevel)

    ShipCost(hullCost, enginesCost, beamsCost, launcherCost, Cost(0, 0, 0, techCost))
  }

  private def lowerLimit(number: Int, limit: Int): Int = {
    if(number<limit) limit else number
  }
}
