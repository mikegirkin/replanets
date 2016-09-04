package replanets.model.trn

import java.nio.charset.Charset
import java.nio.file.Paths
import java.nio.{ByteBuffer, ByteOrder}

import replanets.common._
import replanets.model.Game
import replanets.model.commands.PlayerCommand
import replanets.recipes.{BYTE, DWORD}

import scala.io.{Codec, Source}

object PlanetsWriter {
  val encoder = Charset.forName("ASCII")

  implicit class WordWriter(val it: Short) extends AnyVal {
    def toBytes: Iterable[Byte] = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(it).array()
  }
  implicit class DwordWriter(val it: Int) extends AnyVal {
    def toBytes: Iterable[Byte] = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(it).array()
  }
  implicit class StringWriter(val it: String) extends AnyVal {
    private def padTo(maxLength: Int)(str: String) = {
      if(str.length >= maxLength) str
      else {
        val bldr = new StringBuilder(str)
        bldr.appendAll(Stream.fill[Char](maxLength - str.length)(' '))
        bldr.toString()
      }
    }

    def toBytes(maxLength: Int): Iterable[Byte] = {
      val strToWrite = padTo(maxLength)(it)
      ByteBuffer.allocate(maxLength)
        .put(encoder.encode(strToWrite))
        .array()
    }
  }
}

import replanets.model.trn.PlanetsWriter._
import NumberExtensions._

class TrnWriter(game: Game) {
  import ByteExtensions._

  val turn = game.lastTurn
  val race = game.playingRace

  val reg = new Reg(Paths.get("./testfiles"))
  val defaultPlanetsRegInfo: IndexedSeq[Iterable[Byte]] = IndexedSeq(
    "VGA Planets shareware    ".toBytes(25),
    "Version 3.00             ".toBytes(25)
  )
  val planetsRegInfo = reg.regkey.fold(
    defaultPlanetsRegInfo
  )( rk =>
    if(reg.checkRegKeyValidity(rk)) reg.readRegStr(rk)
    else defaultPlanetsRegInfo
  )

  def write(): Iterable[Byte] = {
    val commandsInBytes = generateCommandsSection(game.turns(game.lastTurn)(game.playingRace).commands)
    val commandsHeader = generateCommandsHeader(commandsInBytes: Iterable[Iterable[Byte]])
    val commandsSection = commandsHeader ++ commandsInBytes.flatten
    val header = generateHeader(commandsInBytes.size)
    val winPlanTrailer = gererateWinPlanTrailer()
    val csX = checksumX(header, commandsSection, winPlanTrailer)
    val dosTrailer = generateDosTrailer(csX)
    val idBlock = generateIdBlock(csX) //TODO: Id block requires all playerX.trn to be generated BEFORE id block is written

    header ++ commandsSection ++ winPlanTrailer ++ dosTrailer ++ idBlock
  }

  private def generateCommandsHeader(commandsSection: Iterable[Iterable[Byte]]): Iterable[Byte] =
  {
    if(commandsSection.isEmpty) Seq()
    else {
      val commandCount = commandsSection.size
      val commandStartPosition = commandCount * 4 + 29 + 1 //29 - size of header , +1 for basic pointers hack
      val commandSizes = commandsSection.map(_.size)
      val commandPositions = commandSizes.scanLeft(commandStartPosition)(_ + _).take(commandCount)

      Seq(0.toByte) ++ commandPositions.flatMap { _.toBytes }
    }
  }

  private def generateHeader(numberOfCommands: Int): Iterable[Byte] = {
    game.playingRace.value.toShort.toBytes ++
    numberOfCommands.toBytes ++
    game.turnSeverData(game.lastTurn).generalInfo.timestamp.toBytes(18) ++
    0.toShort.toBytes ++
    game.turnSeverData(game.lastTurn).generalInfo.timestampChecksum.toBytes
  }

  type TrnRecord = Iterable[Byte]

  private def shipCommand(code: Int, shipId: ShipId) =
    code.toShort.toBytes ++ shipId.value.toShort.toBytes

  private def planetaryCommand(code: Int, planetId: PlanetId) =
    code.toShort.toBytes ++ planetId.value.toShort.toBytes

  private def shipChangeFcode(ship: OwnShip): TrnRecord =
    shipCommand(1, ship.id) ++ ship.fcode.value.toBytes(3)

  private def shipChangeSpeed(ship: OwnShip): TrnRecord =
    shipCommand(2, ship.id) ++ ship.warp.toShort.toBytes

  private def shipChangeWaypoint(ship: OwnShip): TrnRecord =
    shipCommand(3, ship.id) ++ ship.xDistanceToWaypoint.upperBound(3000).toShort.toBytes ++ ship.yDistanceToWaypoint.upperBound(3000).toShort.toBytes

  private def shipChangeMission(ship:OwnShip): TrnRecord =
    shipCommand(4, ship.id) ++ ship.mission.toShort.toBytes

  private def shipChangePrimaryEnemy(ship: OwnShip): TrnRecord =
    shipCommand(5, ship.id) ++ ship.primaryEnemy.toShort.toBytes

  private def shipTowShip(ship: OwnShip): TrnRecord =
    shipCommand(6, ship.id) ++ ship.towShipId.toShort.toBytes

  private def shipChangeName(ship: OwnShip): TrnRecord =
    shipCommand(7, ship.id) ++ ship.name.toBytes(20)

  private def shipBeamDownCargo(ship: OwnShip): TrnRecord =
    shipCommand(8, ship.id) ++ Seq.fill[Short](6)(0).flatMap(_.toBytes) ++ ship.transferToPlanet.targetId.value.toShort.toBytes

  private def shipTransferCargo(ship: OwnShip): TrnRecord =
    shipCommand(9, ship.id) ++ Seq.fill[Short](6)(0).flatMap(_.toBytes) ++ ship.transferToEnemyShip.targetId.value.toShort.toBytes

  private def shipIntercept(ship: OwnShip): TrnRecord =
    shipCommand(10, ship.id) ++ ship.interceptTargetId.toShort.toBytes

  private def shipChangeNeutronium(ship: OwnShip): TrnRecord =
    shipCommand(11, ship.id) ++ ship.minerals.neutronium.toShort.toBytes

  private def shipChangeTritanium(ship: OwnShip): TrnRecord =
    shipCommand(12, ship.id) ++ ship.minerals.tritanium.toShort.toBytes

  private def shipChangeDuranium(ship: OwnShip): TrnRecord =
    shipCommand(13, ship.id) ++ ship.minerals.duranium.toShort.toBytes

  private def shipChangeMolybdenium(ship: OwnShip): TrnRecord =
    shipCommand(14, ship.id) ++ ship.minerals.molybdenium.toShort.toBytes

  private def shipChangeSupplies(ship: OwnShip): TrnRecord =
    shipCommand(15, ship.id) ++ ship.supplies.toShort.toBytes

  private def shipChangeColonists(ship: OwnShip): TrnRecord =
    shipCommand(16, ship.id) ++ ship.colonistClans.toShort.toBytes

  private def shipChangeTorpedoes(ship: OwnShip): TrnRecord =
    shipCommand(17, ship.id) ++ ship.torpsFightersLoaded.toShort.toBytes

  private def shipChangeMoney(ship: OwnShip): TrnRecord =
    shipCommand(18, ship.id) ++ ship.money.toShort.toBytes

  private def planetChangeFcode(planet: Planet): TrnRecord =
    planetaryCommand(21, planet.id) ++ planet.fcode.value.toBytes(3)

  private def planetBuildMines(planet: Planet): TrnRecord =
    planetaryCommand(22, planet.id) ++ planet.minesNumber.toShort.toBytes

  private def planetBuildFactories(planet: Planet): TrnRecord =
    planetaryCommand(23, planet.id) ++ planet.factoriesNumber.toShort.toBytes

  private def planetBuildDefences(planet: Planet): TrnRecord =
    planetaryCommand(24, planet.id) ++ planet.defencesNumber.toShort.toBytes

  private def planetChangeNeutronium(planet: Planet): TrnRecord =
    planetaryCommand(25, planet.id) ++ planet.surfaceMinerals.neutronium.toBytes

  private def planetChangeTritanium(planet: Planet): TrnRecord =
    planetaryCommand(26, planet.id) ++ planet.surfaceMinerals.tritanium.toBytes

  private def planetChangeDuranium(planet: Planet): TrnRecord =
    planetaryCommand(27, planet.id) ++ planet.surfaceMinerals.duranium.toBytes

  private def planetChangeMolibdanium(planet: Planet): TrnRecord =
    planetaryCommand(28, planet.id) ++ planet.surfaceMinerals.molybdenium.toBytes

  private def planetChangeColonists(planet: Planet): TrnRecord =
    planetaryCommand(29, planet.id) ++ planet.colonistClans.toBytes

  private def planetChangeSupplies(planet: Planet): TrnRecord =
    planetaryCommand(30, planet.id) ++ planet.supplies.toBytes

  private def planetChangeMoney(planet: Planet): TrnRecord =
    planetaryCommand(31, planet.id) ++ planet.money.toBytes

  private def planetChangeColonistTax(planet: Planet): TrnRecord =
    planetaryCommand(32, planet.id) ++ planet.colonistTax.toShort.toBytes

  private def planetChangeNativeTax(planet: Planet): TrnRecord =
    planetaryCommand(33, planet.id) ++ planet.nativeTax.toShort.toBytes

  private def planetBuildStarbase(planet: Planet): TrnRecord =
    planetaryCommand(34, planet.id)

  private def checkAndCreateCommand[TObject, T](initialState: TObject, stateAfterCommands: TObject)(valueExtractor: TObject => T, commandGenerator: TObject => TrnRecord): Option[TrnRecord] =
    if(valueExtractor(initialState) != valueExtractor(stateAfterCommands))
      Some(commandGenerator(stateAfterCommands))
    else None



  private def generateCommandsSection(commands: Iterable[PlayerCommand]): Iterable[TrnRecord] = {
    //planetary commands
    val planetaryCommands = game.turnInfo(game.lastTurn).stateAfterCommands.planets.values.toList.sortBy(_.id.value).flatMap { planet =>
      val planetInitialState = game.turnInfo(game.lastTurn).initialState.planets(planet.id)
      val planetCommandCreate = checkAndCreateCommand(planetInitialState, planet) _
      Iterable.empty[TrnRecord] ++
      planetCommandCreate(_.fcode, planetChangeFcode) ++
      planetCommandCreate(_.minesNumber, planetBuildMines) ++
      planetCommandCreate(_.factoriesNumber, planetBuildFactories) ++
      planetCommandCreate(_.defencesNumber, planetBuildDefences) ++
      planetCommandCreate(_.surfaceMinerals.neutronium, planetChangeNeutronium) ++
      planetCommandCreate(_.surfaceMinerals.tritanium, planetChangeTritanium) ++
      planetCommandCreate(_.surfaceMinerals.duranium, planetChangeDuranium) ++
      planetCommandCreate(_.surfaceMinerals.molybdenium, planetChangeMolibdanium) ++
      planetCommandCreate(_.colonistClans, planetChangeColonists) ++
      planetCommandCreate(_.supplies, planetChangeSupplies) ++
      planetCommandCreate(_.money, planetChangeMoney) ++
      planetCommandCreate(_.colonistTax, planetChangeColonistTax) ++
      planetCommandCreate(_.nativeTax, planetChangeNativeTax) ++
      planetCommandCreate(_.buildBase, planetBuildStarbase)
    }
    val shipCommands = game.turnInfo(game.lastTurn).stateAfterCommands.ownShips.values.flatMap { ship =>
      val shipInitialState = game.turnInfo(game.lastTurn).initialState.ownShips(ship.id)
      val shipCommand = checkAndCreateCommand(shipInitialState, ship) _
      Iterable.empty[TrnRecord] ++
      shipCommand(_.fcode, shipChangeFcode) ++
      shipCommand(_.warp, shipChangeSpeed) ++
        (if(ship.xDistanceToWaypoint != shipInitialState.xDistanceToWaypoint || ship.yDistanceToWaypoint != shipInitialState.yDistanceToWaypoint)
        Some(shipChangeWaypoint(ship)) else None) ++
      shipCommand(_.mission, shipChangeMission) ++
      shipCommand(_.primaryEnemy, shipChangePrimaryEnemy) ++
      shipCommand(_.towShipId, shipTowShip) ++
      shipCommand(_.name, shipChangeName) ++
      shipCommand(_.transferToPlanet, shipBeamDownCargo) ++
      shipCommand(_.transferToEnemyShip, shipTransferCargo) ++
      shipCommand(_.interceptTargetId, shipIntercept) ++
      shipCommand(_.minerals.neutronium, shipChangeNeutronium) ++
      shipCommand(_.minerals.tritanium, shipChangeTritanium) ++
      shipCommand(_.minerals.duranium, shipChangeDuranium) ++
      shipCommand(_.minerals.molybdenium, shipChangeMolybdenium) ++
      shipCommand(_.supplies, shipChangeSupplies) ++
      shipCommand(_.colonistClans, shipChangeColonists) ++
      shipCommand(_.torpsFightersLoaded, shipChangeTorpedoes) ++
      shipCommand(_.money, shipChangeMoney)
    }

    val starbaseCommands = Iterable.empty[TrnRecord]

    shipCommands ++ planetaryCommands ++ starbaseCommands
  }

  private def gererateWinPlanTrailer(): Iterable[Byte] = {

    val vph35dll = {
      val source = Source.fromInputStream(getClass.getResourceAsStream("/files/vph35.dll"))(Codec.ISO8859)
      val result = source.map(_.toByte).toArray
      source.close()
      result
    }

    //Formulas from VPA source code
    val vphAValue = DWORD.read(vph35dll.iterator.slice((turn.value - 2) * 4 + 347, (turn.value - 2) * 4 + 347 + 4)) & 0x7FFFFFFF
    val vphBValue = DWORD.read(vph35dll.iterator.slice((turn.value - 2) * 4 + 1857, (turn.value - 2) * 4 + 1857 + 4)) & 0x7FFFFFFF
    val stringA = BYTE.readSome(vph35dll.iterator.drop((turn.value - 2) * 4 + 1857 + 4), 50)

    "VER3.500".toBytes(8) ++
    //VPH35 value part A
    vphAValue.toBytes ++
    //VPH35 value part B
    vphBValue.toBytes ++
    //String 1, part A
    stringA.take(25) ++
    //String 1, part B
    stringA.take(25).zip(planetsRegInfo(0)).map{ case (s1, pri) => (s1 ^ pri).toByte } ++
    //String 2, part A
    stringA.slice(25, 50) ++
    //String 2, part B
    stringA.slice(25, 50).zip(planetsRegInfo(1)).map { case (s2, pri) => (s2 ^ pri).toByte } ++
    //Name of player
    // Address of player
    "Anonymous WinPlan owner".toBytes(100) ++ //TODO: This could be incorrect and required thorough testing
    //Empty space 100 bytes
    Stream.fill[Byte](100)(0)
  }

  def checksumX(header: Iterable[Byte], commandsSection: Iterable[Byte], winPlanTrailer: Iterable[Byte]): Int = {
    //Checksum 'X'
    val timstampChecksum: Int = game.turnSeverData(game.lastTurn).generalInfo.timestampChecksum
    val bytesSum: Int = (header ++ commandsSection ++ winPlanTrailer).map(_.toUnsignedInt).sum
    bytesSum + timstampChecksum * 3 + 13
  }

  def generateDosTrailer(checksumX: Int) = {
    checksumX.toBytes ++
    0.toBytes ++
    fizzbinSignature
  }

  def fizzbinSignature(): Iterable[Byte] = {
    val regstr1: Iterable[Int] = planetsRegInfo(0).zipWithIndex
      .map { case (b, idx) => b * (idx + 1) * 13 }

    val regstr2: Iterable[Int] = planetsRegInfo(1).zipWithIndex
      .map { case (b, idx) => b * (idx + 1) * 13 }

    val data: Iterable[Int] = regstr1 ++ regstr2

    val checksum = data.sum + 668

    (data ++ Seq(checksum)).flatMap { _.toBytes }
  }

  private def generateIdBlock(checksumX: Int): Iterable[Byte] = {
    //Id block
    val idBlock = IndexedSeq.fill[Int](11)(0)
    idBlock.updated(game.playingRace.value - 1, checksumX)
      .flatMap { _.toBytes }
  }

}
