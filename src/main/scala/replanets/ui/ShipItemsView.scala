package replanets.ui

import javafx.scene.{paint => jfxsp}

import replanets.model.{ShipBuildOrder, Starbase}

import scalafx.Includes._
import scalafx.beans.property.ObjectProperty
import scalafx.geometry.Pos
import scalafx.scene.control.Label
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{HBox, Pane, VBox}

class ShipItemsView[T](
  headerText: String,
  base: ObjectProperty[Option[Starbase]],
  selectedItem: ObjectProperty[T],
  itemBeingBuilt: ShipBuildOrder => Option[T],
  things: Seq[T],
  techLevel: T => Int,
  name: T => String,
  baseTech: Starbase => Int,
  storedCount: (Starbase, T, Int) => Int
) extends VBox {

  styleClass = Seq("itemsView")

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
        if(selectedItem.value == thing && base.value.flatMap(_.shipBeingBuilt).map(itemBeingBuilt).contains(Some(thing))) jfxsp.Color.GREENYELLOW
        else if(selectedItem.value == thing) jfxsp.Color.LIMEGREEN
        else if(base.value.flatMap(_.shipBeingBuilt).map(itemBeingBuilt).contains(Some(thing))) jfxsp.Color.GOLD
        else if(techLevel(thing) > base.value.map(baseTech).getOrElse(0)) jfxsp.Color.DARKGRAY
        else jfxsp.Color.WHITE
      }, base, selectedItem)
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

          onMouseClicked = (e: MouseEvent) => selectedItem.value = thing
        }
      )
    }
  }

}
