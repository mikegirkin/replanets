package replanets.common

import replanets.recipes.{SpacePaddedString, WORD}

case class TransferRecord(
  neutronuim: Short,
  tritanium: Short,
  duranium: Short,
  molybdenium: Short,
  colonists: Short,
  supplies: Short,
  targetId: Short
)

case class ShipRecord(
  shipId: Short,
  playerId: Short,
  fcode: String,
  warp: Short,
  xDistanceToWaypoint: Short,
  yDistanceToWaypoint: Short,
  xPosition: Short,
  yPosition: Short,
  engineType: Short,
  hullType: Short,
  beamType: Short,
  numberOfBeams: Short,
  fighterBays: Short,
  torpsType: Short,
  torpsFightersLoaded: Short,
  numberOfTorpLaunchers: Short,
  mission: Short,
  primaryEnemyId: Short,
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
  unloadToPlanet: TransferRecord,
  transferToEnemyShip: TransferRecord,
  secondMissionArgument: Short,
  money: Short
)


object ShipsReader {
  private def readTransferRecord(it: Iterator[Byte]): TransferRecord = {
    TransferRecord(
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it)
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
      SpacePaddedString(20).read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      readTransferRecord(it),
      readTransferRecord(it),
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
