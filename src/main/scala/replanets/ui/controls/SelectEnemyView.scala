package replanets.ui.controls

import replanets.common.{Constants, RaceId, RacenmItem}

import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{ListCell, ListView}
import scalafx.scene.input.MouseEvent
import scalafx.scene.paint.Color
import scalafx.stage.Popup

class SelectEnemyView(
  races: IndexedSeq[RacenmItem],
  onSelect: (Option[RaceId]) => Unit
) extends Popup {

  autoHide = true

  val racesList = new ListView[(Option[RaceId], String)] {
    styleClass = Seq("popupOuter")
    cellFactory = { _ =>
      new ListCell[(Option[RaceId], String)] {
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

  racesList.items = ObservableBuffer(
    Seq((None, "None")) ++
    (0 until Constants.NumberOfRaces).map { idx =>
      (Some(RaceId(idx + 1)), races(idx).shortname)
    }
  )

  content.add(racesList)

  def setSelected(enemyId: Option[RaceId]): Unit = {
    val index = racesList.items.getValue.indexWhere{case(raceIdOpt, _) => raceIdOpt == enemyId}
    racesList.getSelectionModel.select(index)
  }
}
