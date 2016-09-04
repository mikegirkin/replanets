package replanets.common

import replanets.recipes.{SpacePaddedString, WORD}

case class TransferToPlanet(
  neutronuim: Int,
  tritanium: Int,
  duranium: Int,
  molybdenium: Int,
  colonists: Int,
  supplies: Int,
  targetId: PlanetId
)

case class TransferToEnemyShip(
  neutronuim: Int,
  tritanium: Int,
  duranium: Int,
  molybdenium: Int,
  colonists: Int,
  supplies: Int,
  targetId: ShipId
)

case class ShipRecord(
  shipId: Short,
  ownerId: Short,
  fcode: String,
  warp: Short,
  xDistanceToWaypoint: Short,
  yDistanceToWaypoint: Short,
  x: Short,
  y: Short,
  engineTypeId: Id,
  hullTypeId: Short,
  beamType: Short,
  numberOfBeams: Short,
  fighterBays: Short,
  torpsType: Short,
  torpsFightersLoaded: Short,
  numberOfTorpLaunchers: Short,
  mission: Short,
  primaryEnemy: Short,
  towShipId: Short,
  damage: Short,
  crew: Short,
  colonistClans: Short,
  name: String,
  neutronium: Short,
  tritanium: Short,
  duranium: Short,
  molybdenium: Short,
  supplies: Short,
  transferToPlanet: TransferToPlanet,
  transferToEnemyShip: TransferToEnemyShip,
  interceptTargetId: Short,
  money: Short
) extends ObjectWithCoords {
  def loadedMass = neutronium + tritanium + duranium + molybdenium + colonistClans + supplies + torpsFightersLoaded
}


object ShipsReader {
  private def readPlanetTransferRecord(it: Iterator[Byte]): TransferToPlanet = {
    TransferToPlanet(
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      PlanetId(WORD.read(it))
    )
  }

  private def readEnemyShipTransferRecord(it: Iterator[Byte]): TransferToEnemyShip = {
    TransferToEnemyShip(
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      ShipId(WORD.read(it))
    )
  }

  private def readShipRecord(it: Iterator[Byte]): ShipRecord = {
    ShipRecord(
      WORD.read(it),
      WORD.read(it),
      SpacePaddedString(3).read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      Id(WORD.read(it)),
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
      SpacePaddedString(20).read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      readPlanetTransferRecord(it),
      readEnemyShipTransferRecord(it),
      WORD.read(it),
      WORD.read(it)
    )
  }

  def read(it: Iterator[Byte]) = {
    val numrecords = WORD.read(it)
    for(i <- 0 until numrecords)
      yield readShipRecord(it)
  }
}
