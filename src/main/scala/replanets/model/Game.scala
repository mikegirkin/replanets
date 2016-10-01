package replanets.model

import java.nio.file.{Files, Path}

import replanets.common._
import replanets.model.commands.PlayerCommand
import replanets.model.trn.TrnWriter

case class Game(
  name: String,
  gameDb: GameDatabase,
  playingRace: RaceId,
  races: IndexedSeq[RacenmItem],
  specs: Specs
) {
  val turns: Map[TurnId, Map[RaceId, TurnInfo]] = gameDb.loadDb(specs)

  def turnSeverData(turn: TurnId) = {
    turns(turn)(playingRace).initialState
  }

  def turnInfo(turn: TurnId): TurnInfo = {
    turns(turn)(playingRace)
  }

  def lastTurn = turns.keys.maxBy(_.value)

  def addCommand(cmd: PlayerCommand) = turns(lastTurn)(playingRace).addCommand(cmd)

  def saveCommands() = {
    gameDb.saveCommands(lastTurn, playingRace, turns(lastTurn)(playingRace).commands)
  }

  def writeTrnForLastTurn() = {
    val filename = s"player${playingRace.value}.trn"
    val trnWriter = new TrnWriter(this)
    val trnFilepath = gameDb.dbDirectoryPath.resolve(s"${lastTurn.value}").resolve(filename)
    Files.write(trnFilepath, trnWriter.write().toArray)
  }

}

object Game {
  import ResourcesExtension._

  def apply(gameDirectory: Path)(gameDb: GameDatabase): Game = {
    val races = RacenmItem.fromFile(getFromResourcesIfInexistent(gameDirectory.resolve(Constants.racenmFilename), s"/files/${Constants.racenmFilename}"))

    //TODO: Figure out a way to detect hosttype
    val hostType: HostType = PHost4

    val formulas: Formulas = hostType match {
      case THost => THostFormulas
      case PHost3 | PHost4 => PHostFormulas
    }
    val missions = new Missions(gameDb.playingRace, hostType)
    val specs = Specs.fromDirectory(gameDirectory)(formulas, missions)

    Game("Test game", gameDb, gameDb.playingRace, races, specs)
  }
}

