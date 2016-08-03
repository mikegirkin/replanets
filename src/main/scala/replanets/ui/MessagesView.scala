package replanets.ui

import replanets.model.MessageInfo

import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.{HBox, Priority, VBox}

class MessagesView(
  messages: IndexedSeq[MessageInfo]
) extends VBox {

  val buttonArea = new HBox { children = Seq(new Label{
    text = "buttons"
  })}

  val messageArea = new VBox {
    children = Seq(
      new Label("MessageArea")
    )
    style = "-fx-background-color: #5588BB;"
    vgrow = Priority.Always
  }

  children = Seq(
    buttonArea,
    messageArea
  )

  reloadModel()

  private def reloadModel() = {
    buttonArea.children = messages.zipWithIndex.map { case (m, idx) =>
      new Button {
        text = idx.toString
        onAction = { e:ActionEvent => showMessage(idx) }
      }
    }
  }

  private def showMessage(index: Int) = {
    messageArea.children = new Label {
      text = messages(index).text
    }
  }
}


