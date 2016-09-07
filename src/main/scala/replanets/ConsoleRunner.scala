package replanets

import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.file.{Files, Paths}

import replanets.common._
import replanets.recipes.{ArrayRecipe, DWORD, SpacePaddedString, WORD}
import NumberExtensions._
import replanets.model.{Cargo, PHostFormulas, Specs}

object ConsoleRunner {
  def run = {
    val reg = new Reg(Paths.get("./testfiles"))
    val rk = reg.regkey
    val isValid = reg.checkRegKeyValidity(rk.get)
    println(isValid)

    val decoder = Charset.forName("ASCII")
    val strs = reg.readRegStr(rk.get)

    val str1 = decoder.decode(ByteBuffer.wrap(strs(0).toArray)).toString
    val str2 = decoder.decode(ByteBuffer.wrap(strs(1).toArray)).toString

    println(s"$str1, $str2")
  }

  def untrn(filename: String) = {
    val file = Paths.get(filename)
    val bytes = Files.readAllBytes(file)
    val race = WORD.read(bytes.slice(0, 2).iterator)
    val numberOfCommands = DWORD.read(bytes.slice(2, 6).iterator)
    println(s"Race: $race, Number of commands: $numberOfCommands")
    for (
      i <- 0 until numberOfCommands
    ) {
      val commandPosition = DWORD.read(bytes.slice(29 + i * 4, 33 + i * 4).iterator) - 1
      val commandCode = WORD.read(bytes.slice(commandPosition, commandPosition + 2).iterator)
      val it = bytes.iterator.drop(commandPosition + 2)
      if(commandCode.between(1, 20)) {
        val shipId = WORD.read(it)
        commandCode match {
          case 1 =>
            val fcode = SpacePaddedString(3).read(it)
            println(s"$commandCode: Change ship fcode. ShipId: $shipId. Fcode: $fcode")
          case 2 =>
            val speed = WORD.read(it)
            println(s"$commandCode: Change ship warp. ShipId: $shipId. speed: $speed")
          case 3 =>
            val waypoint = (WORD.read(it), WORD.read(it))
            println(s"$commandCode: Change ship waypoint. ShipId: $shipId. WP: $waypoint")
          case 4 =>
            val mission = WORD.read(it)
            println(s"$commandCode: Change ship mission. ShipId: $shipId. Mission: ${new Missions(RaceId(race), PHost4).all(mission)}")
          case 5 =>
            val primaryEnemy = WORD.read(it)
            println(s"$commandCode: Change ship primary enemy. ShipId: $shipId. PE: $primaryEnemy")
          case 6 =>
            val towShipId = WORD.read(it)
            println(s"$commandCode: Ship tow ship. ShipId: $shipId. Tow ship id: $towShipId")
          case 7 =>
            val name = SpacePaddedString(20).read(it)
            println(s"$commandCode: Ship change name. ShipId: $shipId. Name: $name")
          case 8 =>
            val cargo = TransferToPlanet(
              WORD.read(it),
              WORD.read(it),
              WORD.read(it),
              WORD.read(it),
              WORD.read(it),
              WORD.read(it),
              PlanetId(WORD.read(it))
            )
            println(s"$commandCode: Ship beam down cargo. ShipId: $shipId. Data: $cargo")
          case 9 =>
            val cargo = TransferToEnemyShip(
              WORD.read(it),
              WORD.read(it),
              WORD.read(it),
              WORD.read(it),
              WORD.read(it),
              WORD.read(it),
              ShipId(WORD.read(it))
            )
            println(s"$commandCode: Ship transfer cargo. ShipId: $shipId. Data: $cargo")
          case 10 =>
            val interceptId = WORD.read(it)
            println(s"$commandCode: Ship intercept. ShipId: $shipId. Intercept id: $interceptId")
          case 11 =>
            val neu = WORD.read(it)
            println(s"$commandCode: Ship change neutronium. ShipId: $shipId. Value: $neu")
          case 12 =>
            val tri = WORD.read(it)
            println(s"$commandCode: Ship change tritanium. ShipId: $shipId. Value: $tri")
          case 13 =>
            val dur = WORD.read(it)
            println(s"$commandCode: Ship change duranium. ShipId: $shipId. Value: $dur")
          case 14 =>
            val mol = WORD.read(it)
            println(s"$commandCode: Ship change molibdenium. ShipId: $shipId. Value: $mol")
          case 15 =>
            val supp = WORD.read(it)
            println(s"$commandCode: Ship change supplies. ShipId: $shipId. Value: $supp")
          case 16 =>
            val colonists = WORD.read(it)
            println(s"$commandCode: Ship change colonists. ShipId: $shipId. Value: $colonists")
          case 17 =>
            val torps = WORD.read(it)
            println(s"$commandCode: Ship change torpedoes. ShipId: $shipId. Value: $torps")
          case 18 =>
            val money = WORD.read(it)
            println(s"$commandCode: Ship change money. ShipId: $shipId. Value: $money")
        }
      } else if (commandCode.between(21, 39)) {
        val planetId = WORD.read(it)
        commandCode match {
          case 21 =>
            val fcode = SpacePaddedString(3).read(it)
            println(s"$commandCode: Planet change fcode. PlanetId: $planetId. Value $fcode")
          case 22 =>
            val mines = WORD.read(it)
            println(s"$commandCode: Planet change mines. PlanetId: $planetId. Value $mines")
          case 23 =>
            val factories = WORD.read(it)
            println(s"$commandCode: Planet change factories. PlanetId: $planetId. Value $factories")
          case 24 =>
            val defences = WORD.read(it)
            println(s"$commandCode: Planet change defences. PlanetId: $planetId. Value $defences")
          case 25 =>
            val neu = DWORD.read(it)
            println(s"$commandCode: Planet change neu. PlanetId: $planetId. Value $neu")
          case 26 =>
            val tri = DWORD.read(it)
            println(s"$commandCode: Planet change tri. PlanetId: $planetId. Value $tri")
          case 27 =>
            val dur = DWORD.read(it)
            println(s"$commandCode: Planet change dur. PlanetId: $planetId. Value $dur")
          case 28 =>
            val mol = DWORD.read(it)
            println(s"$commandCode: Planet change mol. PlanetId: $planetId. Value $mol")
          case 29 =>
            val colonists = DWORD.read(it)
            println(s"$commandCode: Planet change colonists. PlanetId: $planetId. Value $colonists")
          case 30 =>
            val supplies = DWORD.read(it)
            println(s"$commandCode: Planet change supplies. PlanetId: $planetId. Value $supplies")
          case 31 =>
            val money = DWORD.read(it)
            println(s"$commandCode: Planet change money. PlanetId: $planetId. Value $money")
          case 32 =>
            val colonistsTax = WORD.read(it)
            println(s"$commandCode: Planet change colonists tax. PlanetId: $planetId. Value $colonistsTax")
          case 33 =>
            val nativeTax = WORD.read(it)
            println(s"$commandCode: Planet change native tax. PlanetId: $planetId. Value $nativeTax")
          case 34 =>
            println(s"$commandCode: Build starbase. PlanetId: $planetId")
        }
      } else if (commandCode.between(40, 59)) {
        val baseId = WORD.read(it)
        commandCode match {
          case 40 =>
            val defences = WORD.read(it)
            println(s"$commandCode: Base change defences. BaseId: $baseId. Value: $defences")
          case 41 =>
            val engTech = WORD.read(it)
            println(s"$commandCode: Base change engine tech. BaseId: $baseId. Value: $engTech")
          case 42 =>
            val hullTech = WORD.read(it)
            println(s"$commandCode: Base change hulls tech. BaseId: $baseId. Value: $hullTech")
          case 43 =>
            val beamTech = WORD.read(it)
            println(s"$commandCode: Base change beam tech. BaseId: $baseId. Value: $beamTech")
          case 44 =>
            val engines = ArrayRecipe(9, WORD).read(it)
            println(s"$commandCode: Base build engines. BaseId: $baseId. Value: $engines")
          case 45 =>
            val hulls = ArrayRecipe(20, WORD).read(it)
            println(s"$commandCode: Base build hulls. BaseId: $baseId. Value: $hulls")
          case 46 =>
            val beams = ArrayRecipe(10, WORD).read(it)
            println(s"$commandCode: Base build beams. BaseId: $baseId. Value: $beams")
          case 47 =>
            val launchers = ArrayRecipe(10, WORD).read(it)
            println(s"$commandCode: Base build launchers. BaseId: $baseId. Value: $launchers")
          case 48 =>
            val torpedoes = ArrayRecipe(10, WORD).read(it)
            println(s"$commandCode: Base build torpedoes. BaseId: $baseId. Value: $torpedoes")
          case 49 =>
            val fighters = WORD.read(it)
            println(s"$commandCode: Base build fighters. BaseId: $baseId. Value: $fighters")
          case 50 =>
            val shipId = WORD.read(it)
            println(s"$commandCode: Base fix/recycle ship. BaseId: $baseId. Value: $shipId")
          case 51 =>
            val action = WORD.read(it) match {
              case 0 => "none"
              case 1 => "fix"
              case 2 => "recycle"
            }
            println(s"$commandCode: Base fix/recycle ship action. BaseId: $baseId. Value: $action")
          case 52 =>
            val mission = WORD.read(it)
            println(s"$commandCode: Base change mission. BaseId: $baseId. Value: ${Constants.baseMissions(mission)}")
          case 53 =>
            val shipData = (WORD.read(it), WORD.read(it), WORD.read(it), WORD.read(it), WORD.read(it), WORD.read(it), WORD.read(it))
            println(s"$commandCode: Base build ship. BaseId: $baseId.\n" +
              s"    Hull: ${shipData._1}, Engine: ${shipData._2},\n" +
              s"    Beam: ${shipData._3}, Beam count: ${shipData._4},\n"+ "" +
              s"    Launchers: ${shipData._5}, Launcher count: ${shipData._6}, additional space: ${shipData._7}")
          case 54 =>
            val torpTech = WORD.read(it)
            println(s"$commandCode: Base change torp tech. BaseId: $baseId. Value: $torpTech")
        }
      } else {
        println(commandCode)
      }
    }
  }

  def raceHulls(race: Int, gameDatabasePath: String) = {
    val dir = Paths.get(gameDatabasePath)
    val specs = Specs.fromDirectory(dir)(PHostFormulas, new Missions(RaceId(race), PHost4))

    specs.raceHulls.availableHulls(race - 1).zipWithIndex.foreach { case (hullId, idx) =>
      println(s"${idx + 1}: ${specs.hullSpecs(hullId - 1)}")
    }
  }

  def rstBases(filename: String, specsDir: String) = {
    val specs = Specs.fromDirectory(Paths.get(specsDir))(PHostFormulas, new Missions(RaceId(4), PHost4))
    val rst = RstFileReader.read(Paths.get(filename), specs)

    rst.bases.foreach(b => println(b))
  }
}