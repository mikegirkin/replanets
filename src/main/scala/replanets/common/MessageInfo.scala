package replanets.common

import replanets.recipes._

/**
  * Created by mgirkin on 28/07/2016.
  */
case class MessageInfo(
  messageAddress: Int,
  messageLength: Short,
  messageText: String
) {
  override def toString: String =
    s"MessageInfo($messageAddress, $messageLength, ${messageText.replace("\r", "\\r")})"
}

object MessagesReader {

  def read(rst: IndexedSeq[Byte], messageInfoSectionPointer: Int): IndexedSeq[MessageInfo] = {
    val it = rst.drop(messageInfoSectionPointer).iterator
    val numberOfMessages = WORD.read(it)
    val messageInfos = ArrayRecipe(numberOfMessages, RecordRecipe(DWORD, WORD){case (a1, a2) => (a1, a2)}).read(it)
    messageInfos.map {
      case (start, length) => {
        val encodedMessage = rst.slice(start - 1, start - 1 + length)
        val decodedMessage = encodedMessage.map(x => (x - 13).asInstanceOf[Byte])
        val text = SpacePaddedString(length).read(decodedMessage.iterator)
        MessageInfo(start, length, text)
      }
    }.toIndexedSeq
  }
}
