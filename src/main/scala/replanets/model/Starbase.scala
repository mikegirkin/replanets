package replanets.model

import replanets.common._

case class Starbase(
  id: PlanetId,
  planet: Planet,
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

  import NumberExtensions._

  def shipCostAtStarbase(buildOrder: ShipBuildOrder): ShipCost = {

    val hullCost = if(storedHulls.getOrElse(buildOrder.hull.id, 0) != 0) Cost.zero else buildOrder.hull.cost
    val enginesToBuild = (buildOrder.hull.enginesNumber - storedEngines(buildOrder.engine.id.value - 1)).lowerBound(0)
    val enginesCost = buildOrder.engine.cost.mul(enginesToBuild)
    val beamsToBuild = buildOrder.beams
      .map(bm => (bm.count - storedBeams(bm.spec.id.value - 1)).lowerBound(0))
      .getOrElse(0)
    val beamsCost = buildOrder.beams
      .map(_.spec.cost.mul(beamsToBuild)).getOrElse(Cost.zero)
    val launchersToBuild = buildOrder.launchers
      .map(l => (l.count - storedLaunchers(l.spec.id.value - 1)).lowerBound(0))
      .getOrElse(0)
    val launcherCost = buildOrder.launchers
      .map(_.spec.launcherCost.mul(launchersToBuild)).getOrElse(Cost.zero)

    val techCost =
      THostFormulas.techUpgradeCost(hullsTech, buildOrder.hull.techLevel) +
      THostFormulas.techUpgradeCost(engineTech, buildOrder.engine.techLevel) +
      THostFormulas.techUpgradeCost(beamTech, buildOrder.beams.map(_.spec.techLevel).getOrElse(1)) +
      THostFormulas.techUpgradeCost(torpedoTech, buildOrder.launchers.map(_.spec.techLevel).getOrElse(1))

    ShipCost(hullCost, enginesCost, beamsCost, launcherCost, Cost(0, 0, 0, techCost))
  }

  def remainsAtPlanet(shipCost: ShipCost): Cost = {
    Cost(
      planet.surfaceMinerals.tritanium - shipCost.total.tri,
      planet.surfaceMinerals.duranium - shipCost.total.dur,
      planet.surfaceMinerals.molybdenium - shipCost.total.mol,
      planet.money + planet.supplies - shipCost.total.money
    )
  }

}
