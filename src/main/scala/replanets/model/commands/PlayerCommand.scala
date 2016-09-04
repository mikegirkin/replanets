package replanets.model.commands

import replanets.common._
import replanets.model.Specs

trait PlayerCommand {
  def isReplacableBy(other: PlayerCommand): Boolean
  def isAddDiffToInitialState(initial: ServerData, specs: Specs): Boolean
  def apply(state: ServerData, specs: Specs): ServerData
  def mergeWith(newerCommand: PlayerCommand): PlayerCommand = newerCommand
}