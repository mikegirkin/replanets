package replanets.common

case class ShipCost(
  hullCost: Cost,
  enginesCost: Cost,
  beamsCost: Cost,
  launcherCost: Cost,
  techCost: Cost
) {
  def total: Cost = hullCost.add(enginesCost).add(beamsCost).add(launcherCost).add(techCost)
}

case class Starbase(
  id: PlanetId,
  planet: PlanetRecord,
  owner: RaceId,
  defences: Short,
  damage: Short,
  engineTech: Short,
  hullsTech: Short,
  beamTech: Short,
  torpedoTech: Short,
  storedEngines: IndexedSeq[Short],

  /***
    * hullspec.id => number stored
    */
  storedHulls: Map[HullId, Short],
  storedBeams: IndexedSeq[Short],
  storedLaunchers: IndexedSeq[Short],
  storedTorpedoes: IndexedSeq[Short],
  fightersNumber: Short,
  actionedShipId: Short,
  shipAction: Short,
  primaryOrder: Short,
  buildShipType: Short,
  engineType: Short,
  beamType: Short,
  beamCount: Short,
  launcherType: Short,
  launchersCount: Short
) {

  def shipCostAtStarbase(
    hullspec: HullspecItem,
    engspec: EngspecItem,
    beamspec: BeamspecItem,
    numberOfBeams: Int,
    torpspec: TorpspecItem,
    numberOfLaunchers: Int): ShipCost = {

    val hullCost = if(storedHulls.getOrElse(hullspec.id, 0) == 0) Cost.zero else hullspec.cost
    val enginesToBuild = hullspec.enginesNumber - storedEngines(engspec.id.value - 1) match {
      case x if x < 0 => 0
      case x => x
    }

    ???
  }
}
