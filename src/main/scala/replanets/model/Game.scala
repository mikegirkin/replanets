package replanets.model

import java.nio.file.Path

import replanets.common._

case class Game(
  name: String,
  gameDb: GameDatabase,
  playingRace: Int,
  races: IndexedSeq[RacenmItem],
  map: ClusterMap,
  specs: Specs,
  formulas: Formulas = THostFormulas
) {
  val turns = gameDb.loadDb()

  override def toString: String = {
    String.join(
      sys.props("line.separator") + sys.props("line.separator"),
      name.toString, races.toString, map.toString, specs.toString)
  }

  def turnSeverData(turn: Int) = {
    turns(turn).rstFiles(playingRace)
  }

  def lastTurn = turns.keys.max

  def playingRaceId = playingRace - 1

  def addCommand(cmd: PlayerCommand) = {
    val commands = turns(lastTurn).playerCommands
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
    gameDb.saveCommands(turns.mapValues(ti => ti.playerCommands))
  }

}

object Game {


  def apply(gameDirectory: Path)(gameDb: GameDatabase): Game = {
    val map = ClusterMap.fromDirectory(gameDirectory)
    val specs = Specs.fromDirectory(gameDirectory)
    val races = RacenmItem.fromFile(gameDirectory.resolve(Constants.racenmFilename))

    Game("Test game", gameDb, gameDb.playingRace, races, map, specs)
  }
}

