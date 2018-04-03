package replanets.model.commands.v0

import replanets.common._
import replanets.model.Specs

trait PlayerCommand {
  def isReplacableBy(other: PlayerCommand): Boolean
  def isAddDiffToInitialState(initial: GameState, specs: Specs): Boolean
  def apply(state: GameState, specs: Specs): GameState
  def mergeWith(newerCommand: PlayerCommand): PlayerCommand = newerCommand
}