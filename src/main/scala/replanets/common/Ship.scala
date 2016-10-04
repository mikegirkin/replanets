package replanets.common

import replanets.model.{Cargo, CargoHold}

sealed trait Ship extends ObjectWithCoords {
  val id: ShipId
  val x: Short
  val y: Short
  val owner: RaceId

  def fullMass: Short
  def plannedDestination: Option[IntCoords]
  def name: String
}

case class Contact(
  id: ShipId,
  x: Short,
  y: Short,
  owner: RaceId,
  mass: Short
) extends Ship {
  override def fullMass: Short = mass

  override def name: String = s"Ship #${id.value}"

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
    val dx = (warp * warp * Math.sin(2 * Math.PI * heading.toDouble / 360)).toInt
    val dy = (warp * warp * Math.cos(2 * Math.PI * heading.toDouble / 360)).toInt
    Some(IntCoords(x + dx, y + dy))
  }
}

case class OwnShip(
  id: ShipId,
  owner: RaceId,
  fcode: Fcode,
  warp: Int,
  xDistanceToWaypoint: Int,
  yDistanceToWaypoint: Int,
  x: Short,
  y: Short,
  engines: EngspecItem,
  hull: HullspecItem,
  beams: Option[BeamspecItem],
  numberOfBeams: Short,
  fighterBays: Short,
  torpsType: Option[TorpspecItem],
  torpsFightersLoaded: Int,
  numberOfTorpLaunchers: Short,
  missionId: Int,
  primaryEnemy: Option[RaceId],
  towShipId: Int,
  damage: Short,
  crew: Short,
  colonistClans: Int,
  name: String,
  minerals: Minerals,
  supplies: Int,
  transferToPlanet: Option[TransferToPlanet],
  transferToEnemyShip: Option[TransferToEnemyShip],
  interceptTargetId: Int,
  money: Int
) extends Ship {

  override def fullMass: Short = (cargoMass + minerals.neutronium + hull.mass).toShort
  def cargoMass: Short = (minerals.tritanium + minerals.duranium + minerals.molybdenium + colonistClans + supplies + torpsFightersLoaded).toShort

  override val plannedDestination: Option[IntCoords] = Some(IntCoords(x + xDistanceToWaypoint, y + yDistanceToWaypoint))

  def cargoHold = CargoHold(
    hull.fuelTankSize,
    hull.cargo,
    10000,
    Cargo(
      minerals.neutronium,
      minerals.tritanium,
      minerals.duranium,
      minerals.molybdenium,
      supplies,
      colonistClans,
      money,
      torpsFightersLoaded
    )
  )

  def isCarrier = fighterBays > 0
  def isLauncher = numberOfTorpLaunchers > 0 && torpsType.isDefined
}