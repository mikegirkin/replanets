package replanets.ui.controls

import replanets.ui.MapObject

import scalafx.Includes._
import scalafx.scene.control.{ListCell, ListView}
import scalafx.scene.input.MouseEvent
import scalafx.scene.paint.Color

class ObjectsListView(
  onSelect: MapObject => Unit
) extends ListView[MapObject] {

  val defaultColor = Color.LightGray
  val ownShipColor = Color.Cyan
  val enemyShipColor = Color.Red
  val mineFieldColor = Color.MediumPurple

  cellFactory = { _ =>
    new ListCell[MapObject] {
      styleClass = Seq("objectsListCell")
      item.onChange { (_, _, item) =>
        if(item == null) text = null
        else {
          val color = item match {
            case _ : MapObject.OwnShip => ownShipColor
            case _ : MapObject.Target => enemyShipColor
            case _ : MapObject.Minefield => mineFieldColor
            case _ => defaultColor
          }
          textFill = color
          text = item.displayName
        }
      }
      onMouseClicked = (e: MouseEvent) => if(this.item.value != null) onSelect(this.item.value)
    }
  }
}