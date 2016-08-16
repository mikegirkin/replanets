package replanets.common

import replanets.model.ShipId

sealed trait Ship extends ObjectWithCoords {
  val id: ShipId
  val x: Short
  val y: Short
  val owner: RaceId

  def fullMass: Short
  def plannedDestination: Option[IntCoords]
}

case class Contact(
  id: ShipId,
  x: Short,
  y: Short,
  owner: RaceId,
  mass: Short
) extends Ship {
  override def fullMass: Short = mass

  override val plannedDestination: Option[IntCoords] = None
}

case class Target(
  id: ShipId,
  owner: RaceId,
  warp: Short,
  x: Short,
  y: Short,
  mass: Short,
  hull: HullspecItem,
  heading: Short,
  name: String
) extends Ship {
  override def fullMass: Short = mass

  override val plannedDestination: Option[IntCoords] = {
    val dx = warp * warp * Math.sin(2 * Math.PI * heading / 360).toInt
    val dy = warp * warp * Math.cos(2 * Math.PI * heading / 360).toInt
    Some(IntCoords(x + dx, y + dy))
  }
}

case class OwnShip(
  id: ShipId,
  owner: RaceId,
  fcode: String,
  warp: Short,
  xDistanceToWaypoint: Short,
  yDistanceToWaypoint: Short,
  x: Short,
  y: Short,
  engines: EngspecItem,
  hull: HullspecItem,
  beams: Option[BeamspecItem],
  numberOfBeams: Short,
  fighterBays: Short,
  torpsType: Option[TorpspecItem],
  torpsFightersLoaded: Short,
  numberOfTorpLaunchers: Short,
  mission: Short,
  primaryEnemy: Short,
  towShipId: Short,
  damage: Short,
  crew: Short,
  colonistClans: Short,
  name: String,
  minerals: Minerals,
  supplies: Short,
  unloadToPlanet: TransferRecord,
  transferToEnemyShip: TransferRecord,
  secondMissionArgument: Short,
  money: Short
) extends Ship {
  override def fullMass: Short = (cargoMass + hull.mass).toShort
  def cargoMass: Short = (minerals.neutronium + minerals.tritanium + minerals.duranium + minerals.molybdenium + colonistClans + supplies + torpsFightersLoaded).toShort

  override val plannedDestination: Option[IntCoords] = Some(IntCoords(x + xDistanceToWaypoint, y + yDistanceToWaypoint))
}