package replanets.model

import replanets.common.{RaceId, RstFile}

import collection.mutable

case class TurnInfo(
  rst: RstFile,
  commands: mutable.Buffer[PlayerCommand]
)
