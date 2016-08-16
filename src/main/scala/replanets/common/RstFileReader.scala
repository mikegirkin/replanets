package replanets.common

import java.nio.file.{Files, Path}

import replanets.model.{ShipId, Specs}
import replanets.recipes.{DWORD, SpacePaddedString}

case class ServerData(
  signature: String,
  subversion: String,

  ships: Map[ShipId, Ship],
  planets: IndexedSeq[PlanetRecord],
  bases: IndexedSeq[BaseRecord],
  messages: IndexedSeq[MessageInfo],
  generalInfo: GeneralTurnInformation,
  mineFields: IndexedSeq[MineFieldRecord],
  ionStorms: IndexedSeq[IonStorm],
  explosions: IndexedSeq[ExplosionRecord]
) {
}



object RstFileReader {

  import IteratorExtensions._

  def read(file: Path, specs: Specs) = {
    val buffer = Files.readAllBytes(file)
    val it = buffer.iterator

    val pointers = DWORD.readSome(it, 8)
    val signature = SpacePaddedString(6).read(it)
    val subversion = SpacePaddedString(2).read(it)
    val winplanDataPosition = DWORD.read(it)
    val leechPosition = DWORD.read(it)

    val shipRecords = ShipsReader.read(buffer.iterator.drop(pointers(0) - 1))
    val targets = TargetReader.readFromRst(buffer)
    val planets = PlanetsReader.read(buffer.iterator.drop(pointers(2) - 1))
    val bases = BasesReader.read(buffer.iterator.drop(pointers(3) - 1))
    val messages = MessagesReader.read(buffer, pointers(4) - 1)
    val shipCoords = ShipCoordsReader.read(buffer.iterator.drop(pointers(5) - 1))
    val generalInfo = GeneralDataReader.read(buffer.iterator.drop(pointers(6) - 1))
    //TODO: vcrs

    val winplanDataPointer = buffer.iterator.drop(40).read(DWORD)
    val mineFields = MineFieldsSectionReader.read(buffer.iterator.drop(winplanDataPointer - 1))
    val ionStorms = IonStormReader.read(buffer.iterator.drop(winplanDataPointer - 1 + 500 * 8))
    val explosions = ExplosionsReader.read(buffer)

    val ships = buildShipsMap(specs, shipRecords, targets, shipCoords)

    ServerData(signature, subversion, ships, planets, bases, messages, generalInfo, mineFields, ionStorms, explosions)
  }

  def readGeneralInfo(file: Path) = {
    val buffer = Files.readAllBytes(file)
    val pointers = DWORD.readSome(buffer.iterator, 8)
    GeneralDataReader.read(buffer.iterator.drop(pointers(6) - 1))
  }

  private def buildShipsMap(
    specs: Specs,
    shipsRecords: IndexedSeq[ShipRecord],
    targetRecords: IndexedSeq[TargetRecord],
    shipCoords: IndexedSeq[ShipCoordsRecord]): Map[ShipId, Ship] = {

    shipCoords.map { sc =>
      shipsRecords.find(sr => sr.shipId == sc.id).map{ sr =>
        ShipId(sc.id) -> OwnShip(
          ShipId(sr.shipId),
          RaceId(sr.ownerId),
          sr.fcode,
          sr.warp,
          sr.xDistanceToWaypoint,
          sr.yDistanceToWaypoint,
          sr.x,
          sr.y,
          specs.engineSpecs(sr.engineTypeId.value - 1),
          specs.hullSpecs(sr.hullTypeId - 1),
          if(sr.beamType > 0) Some(specs.beamSpecs(sr.beamType - 1)) else None,
          sr.numberOfBeams,
          sr.fighterBays,
          if(sr.torpsType > 0) Some(specs.torpSpecs(sr.torpsType - 1)) else None,
          sr.torpsFightersLoaded,
          sr.numberOfTorpLaunchers,
          sr.mission,
          sr.primaryEnemy,
          sr.towShipId,
          sr.damage,
          sr.crew,
          sr.colonistClans,
          sr.name,
          Minerals(sr.neutronium, sr.tritanium, sr.duranium, sr.molybdenium),
          sr.supplies,
          sr.unloadToPlanet,
          sr.transferToEnemyShip,
          sr.secondMissionArgument,
          sr.money
        )
      }.getOrElse {
        targetRecords.find(tr => tr.shipId == sc.id).map { tr =>
          ShipId(sc.id) -> Target(
            ShipId(tr.shipId),
            RaceId(tr.owner),
            tr.warp,
            tr.x,
            tr.y,
            sc.mass,
            specs.hullSpecs(tr.hullType),
            tr.heading,
            tr.name
          )
        }.getOrElse{
          ShipId(sc.id) -> Contact(
            ShipId(sc.id),
            sc.x,
            sc.y,
            RaceId(sc.owner),
            sc.mass
          )
        }
      }
    }.toMap
  }

}