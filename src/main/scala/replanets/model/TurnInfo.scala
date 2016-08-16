package replanets.model

import replanets.common.ServerData

import scala.collection.mutable

case class TurnInfo(
  rst: ServerData,
  commands: mutable.Buffer[PlayerCommand]
)
