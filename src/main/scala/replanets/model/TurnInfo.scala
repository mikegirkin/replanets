package replanets.model

import replanets.common.RstFile

import collection.mutable
import scala.collection.mutable.ArrayBuffer

case class TurnInfo(
  rstFiles: Map[Int, RstFile],
  playerCommands: mutable.ArrayBuffer[PlayerCommand] = ArrayBuffer()
)
