package replanets.ui.controls

import replanets.common.{MissionRequirement, Ship}

import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.geometry.{HPos, Pos}
import scalafx.scene.control._
import scalafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
import scalafx.scene.layout.{ColumnConstraints, GridPane, VBox}
import scalafx.scene.paint.Color
import scalafx.stage.Popup

case class SelectedMission(
  missionId: Int,
  interceptArgument: Int = 0,
  towArgument: Int = 0
)

class SelectMissionView(
  missionDesctioptions: Map[Int, MissionRequirement],
  missions: Iterable[(Int, String)],
  onSelect: (SelectedMission) => Unit,
  shipsAtPosition: () => Seq[Ship]
) extends Popup {

  autoHide = true

  val popup = this

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
        onMouseClicked = (e: MouseEvent) => if (this.item.value != null) onMissionSelected(this.item.value._1)
      }
    }
  }

  missionList.items = ObservableBuffer(missions.toSeq.sortBy{case (id, _) => id})

  content.setAll(missionList)

  val towTargetSelect = new VBox {
    styleClass = Seq("popupOuter")
    var onItemMouseClicked: (Int) => Unit = (_) => Unit

    val backButton = new Button("< Back")
    val towShipList = new ListView[Ship] {
      cellFactory = { _ =>
        new ListCell[Ship] {
          styleClass = Seq("objectsListCell")
          item.onChange { (_, _, item) =>
            if (item == null) text = null
            else {
              textFill = Color.LimeGreen
              text = item.name
            }
          }
          onMouseClicked = (e: MouseEvent) => if (this.item.value != null) onItemMouseClicked(this.item.value.id.value)
        }
      }
    }

    backButton.onAction = (e: ActionEvent) => content.setAll(missionList)

    children = Seq(backButton, towShipList)
  }

  val mitControl = new VBox {
    styleClass = Seq("popupOuter")
    maxWidth <== popup.width

    val backButton = new Button {
      text = "< Back"
      onAction = (e: ActionEvent) => content.setAll(missionList)
    }

    var onFinished: () => Unit = () => Unit

    val lblIntercept = new Label("Intercept Id:")
    val edIntercept = new TextField {
      onKeyPressed = (e: KeyEvent) => {
        if(e.code == KeyCode.Enter && edTow.visible.value) {
          edTow.requestFocus()
        } else if (e.code == KeyCode.Enter) {
          onFinished()
        }
      }
    }
    val lblTow = new Label("Tow id:")
    val edTow = new TextField {
      onKeyPressed = (e: KeyEvent) => if(e.code == KeyCode.Enter) onFinished()
    }

    private val grid = new GridPane {
      columnConstraints = Seq(
        new ColumnConstraints {
          percentWidth = 60
          halignment = HPos.Right
          alignment = Pos.CenterRight
        },
        new ColumnConstraints {
          percentWidth = 40
          alignment = Pos.CenterLeft
        }
      )

      add(lblIntercept, 0, 0)
      add(edIntercept, 1, 0)
      add(lblTow, 0, 1)
      add(edTow, 1, 1)
    }

    children = Seq(backButton, grid)

    def setInterceptVisibility(visible: Boolean) = {
      lblIntercept.visible = visible
      edIntercept.visible = visible
    }

    def setTowVisibility(visible: Boolean) = {
      lblTow.visible = visible
      edTow.visible = visible
    }
  }

  def reset() = {
    content.setAll(missionList)
  }

  def setSelectedItem(missionId: Int) = {
    val index = missionList.getItems.indexWhere{ case (mId, _) => mId == missionId }
    missionList.getSelectionModel.select(index)
  }

  val towMissionId = 7
  val interceptMissionId = 8

  private def onMissionSelected(missionId: Int): Unit = {
    if(missionId == towMissionId) {
      //Tow
      onTow()
    } else if(missionId == interceptMissionId) {
      //Intercept
      onIntercept()
    } else if(missionId >= 20) {
      //Extended missions
      onExtendedMission(missionId)
    } else {
      onSelect(SelectedMission(missionId))
    }
  }

  private def onTow() = {
    towTargetSelect.towShipList.items = ObservableBuffer(shipsAtPosition())
    towTargetSelect.onItemMouseClicked = (targetId) => {
      onSelect(SelectedMission(towMissionId, towArgument = targetId))
      content.setAll(missionList)
    }
    content.setAll(towTargetSelect)
  }

  private def onIntercept() = {
    mitControl.setTowVisibility(false)
    mitControl.setInterceptVisibility(true)
    mitControl.lblIntercept.text = "Ship id to intercept:"
    mitControl.onFinished = () => {
      val interceptId = mitControl.edIntercept.text.value.toInt
      onSelect(SelectedMission(interceptMissionId, interceptId))
    }
    content.setAll(mitControl)
    mitControl.edIntercept.requestFocus()
  }

  private def onExtendedMission(missionId: Int) = {
    val requirements = missionDesctioptions.get(missionId)
    requirements.fold(
      onSelect(SelectedMission(missionId))
    ) { r =>
      r.tow.foreach { ta =>
        mitControl.lblTow.text = ta.text
      }
      r.itercept.foreach { ia =>
        mitControl.lblIntercept.text = ia.text
      }
      mitControl.setTowVisibility(r.tow.isDefined)
      mitControl.setInterceptVisibility(r.itercept.isDefined)
      mitControl.onFinished = () => {
        val interceptId = if(r.itercept.isDefined) mitControl.edIntercept.text.value.toInt else 0
        val towId = if(r.tow.isDefined) mitControl.edTow.text.value.toInt else 0
        onSelect(SelectedMission(missionId, interceptId, towId))
      }
      content.setAll(mitControl)
      if(r.itercept.isDefined) mitControl.edIntercept.requestFocus() else mitControl.edTow.requestFocus()
    }
  }
}