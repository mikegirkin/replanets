package replanets.model.trn

import java.nio.charset.Charset
import java.nio.file.Paths
import java.nio.{ByteBuffer, ByteOrder}

import replanets.common.{ByteExtensions, PlanetId, Reg}
import replanets.model.{Game, PlayerCommand}
import replanets.recipes.{BYTE, DWORD}
import replanets.ui.viewmodels.PlanetInfoVM

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

  private def generateCommandsSection(commands: Iterable[PlayerCommand]): Iterable[Iterable[Byte]] = {
    val changedPlanetIds = commands.map(_.objectId).filter(_.isInstanceOf[PlanetId]).map(_.value).toSeq.distinct

    for (
      planetId <- changedPlanetIds;
      initialState <- game.turnSeverData(game.lastTurn).planets.find(p => p.planetId == planetId);
      resultState = new PlanetInfoVM(game, game.lastTurn, planetId)
    ) yield {
      if(resultState.fcode != initialState.fcode) 21.toShort.toBytes ++ planetId.toShort.toBytes ++ resultState.fcode.value.toBytes(3)
      else Seq()
    }
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
