package replanets.model.trn

import java.nio.charset.Charset
import java.nio.file.Paths
import java.nio.{ByteBuffer, ByteOrder}

import replanets.common._
import replanets.model.{Game, Starbase}
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

  private def checkAndCreateCommand[TObject, T](initialState: TObject, stateAfterCommands: TObject)(valueExtractor: TObject => T, commandGenerator: TObject => TrnRecord): Option[TrnRecord] =
    if(valueExtractor(initialState) != valueExtractor(stateAfterCommands))
      Some(commandGenerator(stateAfterCommands))
    else None

  private def recordFor[TObject, T](id: TObject => Short)(current: TObject, initial: TObject)(commandCode: Short, valueExtractor: TObject => T)(itemWriter: T => Iterable[Byte]): Option[TrnRecord] = {
    if(valueExtractor(initial) != valueExtractor(current)) Some(
      commandCode.toBytes ++ id(current).toBytes ++ itemWriter(valueExtractor(current))
    ) else None
  }

  private def shipRecordFor[T] = recordFor[OwnShip, T](_.id.value.toShort) _
  private def planetRecordFor[T] = recordFor[Planet, T](_.id.value.toShort) _
  private def strarbaseRecordFor[T] = recordFor[Starbase, T](_.id.value.toShort) _

  implicit val shortsWriter: Short => Iterable[Byte] = _.toBytes
  implicit val intsWriter: Int => Iterable[Byte] = _.toBytes
  implicit val fcodeWriter: Fcode => Iterable[Byte] = _.value.toBytes(3)
  implicit val arrayShortsWriter: IndexedSeq[Short] => Iterable[Byte] = seq => seq.flatMap(_.toBytes)

  private def generateCommandsSection(commands: Iterable[PlayerCommand]): Iterable[TrnRecord] = {
    val gameInitialState = game.turnInfo(game.lastTurn).initialState
    val stateAfterCommands = game.turnInfo(game.lastTurn).stateAfterCommands

    val planetaryCommands = stateAfterCommands.planets.values.toList.sortBy(_.id.value).flatMap { planet =>
      val planetInitialState = gameInitialState.planets(planet.id)
      def command[T](commandCode: Short, valueExtractor: Planet => T)(implicit itemWriter: T => Iterable[Byte]) = planetRecordFor(planetInitialState, planet)(commandCode, valueExtractor)(itemWriter)
      Iterable.empty[TrnRecord] ++
      command(21, _.fcode) ++
      command(22, _.minesNumber.toShort) ++
      command(23, _.factoriesNumber.toShort) ++
      command(24, _.defencesNumber.toShort) ++
      command(25, _.surfaceMinerals.neutronium) ++
      command(26, _.surfaceMinerals.tritanium) ++
      command(27, _.surfaceMinerals.duranium) ++
      command(28, _.surfaceMinerals.molybdenium) ++
      command(29, _.colonistClans) ++
      command(30, _.supplies) ++
      command(31, _.money) ++
      command(32, _.colonistTax.toShort) ++
      command(33, _.nativeTax.toShort) ++
      command(34, _.buildBase.toShort)
    }
    val shipCommands = stateAfterCommands.ownShips.values.toList.sortBy(_.id.value).flatMap { ship =>
      val shipInitialState = gameInitialState.ownShips(ship.id)
      def command[T](commandCode: Short, valueExtractor: OwnShip => T)(implicit itemWriter: T => Iterable[Byte]) = shipRecordFor(shipInitialState, ship)(commandCode, valueExtractor)(itemWriter)
      Iterable.empty[TrnRecord] ++
      command(1, _.fcode) ++
      command(2, _.warp.toShort) ++
      (if(ship.xDistanceToWaypoint != shipInitialState.xDistanceToWaypoint || ship.yDistanceToWaypoint != shipInitialState.yDistanceToWaypoint)
        Some(3.toShort.toBytes ++ ship.id.value.toShort.toBytes ++ ship.xDistanceToWaypoint.toShort.toBytes ++ ship.yDistanceToWaypoint.toShort.toBytes)
      else None) ++
      command(4, _.mission.toShort) ++
      command(5, _.primaryEnemy.toShort) ++
      command(6, _.towShipId.toShort) ++
      command(7, _.name)(_.toBytes(20)) ++
      command(8, _.transferToPlanet)(t => Seq.fill[Short](6)(0).flatMap(_.toBytes) ++ t.targetId.value.toShort.toBytes) ++
      command(9, _.transferToEnemyShip)(t => Seq.fill[Short](6)(0).flatMap(_.toBytes) ++ t.targetId.value.toShort.toBytes) ++
      command(10, _.interceptTargetId.toShort) ++
      command(11, _.minerals.neutronium.toShort) ++
      command(12, _.minerals.tritanium.toShort) ++
      command(13, _.minerals.duranium.toShort) ++
      command(14, _.minerals.molybdenium.toShort) ++
      command(15, _.supplies.toShort) ++
      command(16, _.colonistClans.toShort) ++
      command(17, _.torpsFightersLoaded.toShort) ++
      command(18, _.money.toShort)
    }

    val starbaseCommands: Iterable[TrnRecord] = stateAfterCommands.bases.values.toList.sortBy(_.id.value).flatMap { base =>
      val baseInitialState = gameInitialState.bases(base.id)
      def command[T](commandCode: Short, valueExtractor: Starbase => T)(implicit itemWriter: T => Iterable[Byte]) = strarbaseRecordFor(baseInitialState, base)(commandCode, valueExtractor)(itemWriter)
      Iterable.empty[TrnRecord] ++
      command(40, _.defences.toShort) ++
      command(41, _.engineTech.toShort) ++
      command(42, _.hullsTech.toShort) ++
      command(43, _.beamTech.toShort) ++
      command(44, _.storedEngines.map(_.toShort)) ++
      (if(base.storedHulls != baseInitialState.storedHulls)
        Some(
          45.toShort.toBytes ++
          base.id.value.toShort.toBytes ++
          game.specs.raceHulls.getRaceHullIds(game.playingRace).flatMap(hullIdx => base.storedHulls(HullId(hullIdx)).toShort.toBytes))
        else None) ++
      command(46, _.storedBeams.map(_.toShort)) ++
      command(47, _.storedLaunchers.map(_.toShort)) ++
      command(48, _.storedTorpedoes.map(_.toShort)) ++
      command(49, _.fightersNumber.toShort) ++
      command(50, _.actionedShipId.toShort) ++
      command(51, _.shipAction.toShort) ++
      command(52, _.primaryOrder.toShort) ++
      //This is hack according to docs http://www.phost.de/~stefan/planets/filefmt.txt.gz
      base.shipBeingBuilt.map( order =>
        53.toShort.toBytes ++
        base.id.value.toShort.toBytes ++
        (game.specs.raceHulls.availableHulls(game.playingRace.value - 1).map(idx => game.specs.hullSpecs(idx - 1)).indexWhere(_ == order.hull) + 1).toShort.toBytes ++
        order.engine.id.value.toShort.toBytes ++
        order.beams.map(_.spec.id.value).getOrElse(0).toShort.toBytes ++
        order.beams.map(_.count).getOrElse(0).toShort.toBytes ++
        order.launchers.map(_.spec.id.value).getOrElse(0).toShort.toBytes ++
        order.launchers.map(_.count).getOrElse(0).toShort.toBytes ++
        0.toShort.toBytes
      ) ++
      command(54, _.torpedoTech.toShort)
    }

    //TODO: Message commands
    //TODO: Phost-special commands


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
