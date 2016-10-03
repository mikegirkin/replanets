package replanets.ui.controls

import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{ListCell, ListView}
import scalafx.scene.input.MouseEvent
import scalafx.scene.paint.Color
import scalafx.stage.Popup

class SelectMissionView(
  missions: Iterable[(Int, String)],
  onSelect: (Int) => Unit
) extends Popup {

  val missionList = new ListView[(Int, String)] {
    cellFactory = { _ =>
      new ListCell[(Int, String)] {
        styleClass = Seq("objectsListCell")
        item.onChange { (_, _, item) =>
          if (item == null) text = null
          else {
            textFill = Color.Cyan
            text = item._2
          }
        }
        onMouseClicked = (e: MouseEvent) => if (this.item.value != null) onSelect(this.item.value._1)
      }
    }
  }

  missionList.items = ObservableBuffer(missions.toSeq.sortBy{case (id, _) => id})

  content.add(missionList)

  def setSelectedItem(missionId: Int) = {
    val index = missionList.getItems.indexWhere{ case (mId, _) => mId == missionId }
    missionList.getSelectionModel.select(index)
  }
}