package replanets.model

import replanets.common.RstFile

case class TurnInfo(
  rstFiles: Map[Int, RstFile],
  playerCommands: IndexedSeq[PlayerCommand] = IndexedSeq()
)

case class PlayerCommand(

)
