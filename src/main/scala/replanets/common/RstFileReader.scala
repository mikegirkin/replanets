package replanets.common

import java.nio.file.{Files, Path}

import replanets.recipes.{DWORD, SpacePaddedString}

case class RstFile(
  pointers: IndexedSeq[Int],
  signature: String,
  subversion: String,
  winplanDataPosition: Int,
  leechPosition: Int,

  ships: IndexedSeq[ShipRecord],
  targets: IndexedSeq[TargetRecord],
  planets: IndexedSeq[PlanetRecord],
  bases: IndexedSeq[BaseRecord],
  messages: IndexedSeq[MessageInfo],
  shipCoords: IndexedSeq[ShipCoordsRecord],
  generalInfo: GeneralTurnInformation,
  ionStorms: IndexedSeq[IonStorm]
)

object RstFileReader {

  import IteratorExtensions._

  def read(file: Path) = {
    val buffer = Files.readAllBytes(file)
    val it = buffer.iterator

    val pointers = DWORD.readSome(it, 8)
    val signature = SpacePaddedString(6).read(it)
    val subversion = SpacePaddedString(2).read(it)
    val winplanDataPosition = DWORD.read(it)
    val leechPosition = DWORD.read(it)

    val ships = ShipsReader.read(buffer.iterator.drop(pointers(0) - 1))
    val targets = TargetReader.read(buffer.iterator.drop(pointers(1) - 1))
    val planets = PlanetsReader.read(buffer.iterator.drop(pointers(2) - 1))
    val bases = BasesReader.read(buffer.iterator.drop(pointers(3) - 1))
    val messages = MessagesReader.read(buffer, pointers(4) - 1)
    val shipCoords = ShipCoordsReader.read(buffer.iterator.drop(pointers(5) - 1))
    val generalInfo = GeneralDataReader.read(buffer.iterator.drop(pointers(6) - 1))
    //vcrs

    val winplanDataPointer = buffer.iterator.drop(40).read(DWORD)
    val ionStorms = IonStormReader.read(buffer.iterator.drop(winplanDataPointer - 1 + 500 * 8))

    RstFile(pointers, signature, subversion, winplanDataPosition, leechPosition, ships, targets, planets, bases, messages, shipCoords, generalInfo, ionStorms)
  }

}