package replanets.model

import replanets.common._
import replanets.model.commands._

import scala.collection.mutable

case class TurnInfo(
  specs: Specs,
  initialState: ServerData
) {

  private val _commands: mutable.Buffer[PlayerCommand] = mutable.Buffer()

  private var _stateAfterCommands: ServerData = initialState

  def stateAfterCommands: ServerData = _stateAfterCommands

  def commands: Seq[PlayerCommand] = Seq(_commands:_*)

  def addCommand(cmds: PlayerCommand*): Unit = {
    cmds.foreach( cmd => {
      val oldCommandIndex = _commands.indexWhere(p => p.isReplacableBy(cmd))
      val changesSomething = cmd.isAddDiffToInitialState(initialState, specs)
      if (oldCommandIndex >= 0 && changesSomething)
        _commands(oldCommandIndex) = cmd
      else if (oldCommandIndex >= 0 && !changesSomething)
        _commands.remove(oldCommandIndex)
      else if (changesSomething)
        _commands.append(cmd)
    })
    _stateAfterCommands = commands.foldLeft(initialState)((state, command) => command.apply(state, specs))
  }

  def withCommands(cmds: PlayerCommand*): TurnInfo = {
    addCommand(cmds:_*)
    this
  }

  def getStarbaseState(baseId: PlanetId): Starbase = {
    stateAfterCommands.bases(baseId)
  }

  def getStarbaseInitial(baseId: PlanetId): Starbase = {
    initialState.bases(baseId)
  }

  def getPlanetState(planetId: PlanetId): PlanetRecord = {
    stateAfterCommands.planets(planetId)
  }

  def getPlanetInitial(planetId: PlanetId): PlanetRecord = {
    initialState.planets(planetId)
  }
}
