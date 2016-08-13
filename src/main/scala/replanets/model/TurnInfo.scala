package replanets.model

import replanets.common.RstFile

import scala.collection.mutable

case class TurnInfo(
  rst: RstFile,
  commands: mutable.Buffer[PlayerCommand]
)
