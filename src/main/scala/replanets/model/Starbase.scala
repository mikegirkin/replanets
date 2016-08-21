package replanets.model

import replanets.common._

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

  def remainsAtPlanet(shipCost: ShipCost): Cost = {
    Cost(
      planet.surfaceMinerals.tritanium - shipCost.total.tri,
      planet.surfaceMinerals.duranium - shipCost.total.dur,
      planet.surfaceMinerals.molybdenium - shipCost.total.mol,
      planet.money + planet.supplies - shipCost.total.money
    )
  }

  private def lowerLimit(number: Int, limit: Int): Int = {
    if(number<limit) limit else number
  }

  def applyCommand(command: PlayerCommand)(specs: Specs): Starbase = {
    command match {
      case x: StartShipConstruction if x.objectId == this.id => {
        val order = x.getBuildOrder(specs)
        val hullTechLevel = Math.max(order.hull.techLevel, this.hullsTech)
        val engineTechLevel = Math.max(order.engine.techLevel, this.engineTech)
        val beamTechLevel = Math.max(order.beam.techLevel, this.beamTech)
        val torpsTechLevel = Math.max(order.launchers.techLevel, this.torpedoTech)
        this.copy(
          shipBeingBuilt = Some(x.getBuildOrder(specs)),
          hullsTech = hullTechLevel,
          engineTech = engineTechLevel,
          beamTech = beamTechLevel,
          torpedoTech = torpsTechLevel
        )
      }
      case x: StopShipConstruction if x.objectId == this.id => {
        this.copy(shipBeingBuilt = None)
      }
      case _ => this
    }
  }
}
