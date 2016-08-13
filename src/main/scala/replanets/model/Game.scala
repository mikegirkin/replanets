package replanets.model

import java.nio.file.{Files, Path}

import replanets.common._
import replanets.model.trn.TrnWriter

case class Game(
  name: String,
  gameDb: GameDatabase,
  playingRace: RaceId,
  races: IndexedSeq[RacenmItem],
  map: ClusterMap,
  specs: Specs,
  formulas: Formulas = THostFormulas
) {
  val turns: Map[TurnId, Map[RaceId, TurnInfo]] = gameDb.loadDb()

  override def toString: String = {
    String.join(
      sys.props("line.separator") + sys.props("line.separator"),
      name.toString, races.toString, map.toString, specs.toString)
  }

  def turnSeverData(turn: TurnId) = {
    turns(turn)(playingRace).rst
  }

  def lastTurn = turns.keys.maxBy(_.value)

  def addCommand(cmd: PlayerCommand) = {
    val commands = turns(lastTurn)(playingRace).commands
    val oldCommandIndex = commands.indexWhere(p => p.isReplacableBy(cmd))
    val changesSomething = cmd.changesSomething(this, lastTurn, playingRace)
    if(oldCommandIndex >= 0 && changesSomething)
      commands(oldCommandIndex) = cmd
    else if (oldCommandIndex >= 0 && !changesSomething)
      commands.remove(oldCommandIndex)
    else if (changesSomething)
      commands.append(cmd)
  }

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
    val map = ClusterMap.fromDirectory(gameDirectory)
    val specs = Specs.fromDirectory(gameDirectory)
    val races = RacenmItem.fromFile(getFromResourcesIfInexistent(gameDirectory.resolve(Constants.racenmFilename), s"/files/${Constants.racenmFilename}"))

    Game("Test game", gameDb, RaceId(gameDb.playingRace), races, map, specs)
  }
}

