package replanets.ui

import javafx.scene.{paint => jfxsp}

import replanets.common.Starbase

import scalafx.Includes._
import scalafx.beans.property.{IntegerProperty, ObjectProperty}
import scalafx.geometry.Pos
import scalafx.scene.control.Label
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{HBox, Pane, VBox}

class ShipItemsView[T](
  headerText: String,
  base: ObjectProperty[Option[Starbase]],
  things: Seq[T],
  techLevel: T => Int,
  name: T => String,
  baseTech: Starbase => Int,
  storedCount: (Starbase, T, Int) => Int
) extends VBox {

  styleClass = Seq("itemsView")

  val selectedItemIndex = IntegerProperty(0)
  val selectedItem = new ObjectProperty[T]()
  selectedItem <== createObjectBinding[T](() => things(selectedItemIndex.value), selectedItemIndex)

  children = Seq(
    new Label(headerText),
    new VBox {
      val items = createRows()
      children = items.map { case (idx, pane) =>
        pane
      }
    }
  )

  private def createRows(): Seq[(Int, Pane)] = {
    things.zipWithIndex.map { case (thing, idx) =>
      val colorBinding = createObjectBinding[jfxsp.Color](() => {
        if(selectedItemIndex.value == idx) jfxsp.Color.LIMEGREEN
        else if(techLevel(thing) > base.value.map(baseTech).getOrElse(0)) jfxsp.Color.DARKGRAY
        else jfxsp.Color.WHITE
      }, base, selectedItemIndex)
      (
        idx,
        new HBox {
          children = Seq(
            new Label {
              text = name(thing)
              textFill <== colorBinding
            },
            new Label {
              text <== createStringBinding(() => {
                val count = base.value.map(b => storedCount(b, thing, idx)).getOrElse(0)
                if(count > 0) s"[$count]" else ""
              }, base)
              textFill <== colorBinding
              alignment = Pos.CenterRight
            }
          )
          minHeight = 21
          maxHeight = 21
          alignment = Pos.CenterLeft
          styleClass = Seq("itemsListItem")

          onMouseClicked = (e: MouseEvent) => selectedItemIndex.value = idx
        }
      )
    }
  }

}
