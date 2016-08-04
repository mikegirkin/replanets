package replanets.model

import replanets.common.RstFile

case class TurnInfo(
  serverReceiveState: ReceivedState,
  playerCommands: IndexedSeq[PlayerCommand] = IndexedSeq()
)

case class PlayerCommand(

)

case class ReceivedState(
  rstFiles: Map[Int, RstFile]
)
