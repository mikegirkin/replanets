package replanets.model

import java.nio.file.Path

import replanets.common._

case class Game(
  name: String,
  dataPath: Path,
  playingRace: Int,
  races: IndexedSeq[RacenmItem],
  map: ClusterMap,
  specs: Specs,
  turns: Map[Int, TurnInfo],
  formulas: Formulas = THostFormulas
) {
  override def toString: String = {
    String.join(
      sys.props("line.separator") + sys.props("line.separator"),
      name.toString, dataPath.toString, races.toString, map.toString, specs.toString)
  }

  def turnSeverData(turn: Int) = {
    turns(turn).rstFiles(playingRace)
  }

  def lastTurn = turns.keys.max

  def playingRaceId = playingRace - 1

  def addCommand(cmd: PlayerCommand) = {
    val commands = turns(lastTurn).playerCommands
    val oldCommandIndex = commands.indexWhere(p => p.isReplacableBy(cmd))
    val changesSomething = cmd.changesSomething(this, lastTurn, playingRaceId)
    if(oldCommandIndex >= 0 && changesSomething)
      commands(oldCommandIndex) = cmd
    else if (oldCommandIndex >= 0 && !changesSomething)
      commands.remove(oldCommandIndex)
    else if (changesSomething)
      commands.append(cmd)
  }
}

object Game {
  def apply(gameDirectory: Path)(gameDb: GameDatabase): Game = {
    val map = ClusterMap.fromDirectory(gameDirectory)
    val specs = Specs.fromDirectory(gameDirectory)
    val races = RacenmItem.fromFile(gameDirectory.resolve(Constants.racenmFilename))

    val turns = gameDb.loadDb()

    Game("Test game", gameDirectory, 1, races, map, specs, turns)
  }
}