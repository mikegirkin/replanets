package replanets.ui.viewmodels

import replanets.common.TurnId
import replanets.ui.{Event, MapObject, MapObjectType}

/**
  * Created by mgirkin on 09/08/2016.
  */
case class ViewModel (
  var turnShown: TurnId,
  private var _selectedObject: Option[MapObject]
) {

  def selectedObject_=(value: Option[MapObject]): Unit = {
    _selectedObject = value
    selectedObjectChaged.fire()
  }

  def selectedObject = _selectedObject

  val selectedObjectChaged = Event[Unit]

  val objectChanged = Event[(MapObjectType, Int)]
}
