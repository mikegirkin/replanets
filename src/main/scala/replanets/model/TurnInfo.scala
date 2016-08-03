package replanets.model

case class TurnInfo(
  serverReceiveState: ReceivedState,
  playerCommands: IndexedSeq[PlayerCommand] = IndexedSeq()
)

case class PlayerCommand(

)

case class ReceivedState(
  messages: IndexedSeq[MessageInfo]
)

case class MessageInfo(
  text: String
)
