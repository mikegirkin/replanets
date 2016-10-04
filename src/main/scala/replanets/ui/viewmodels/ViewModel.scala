package replanets.ui.viewmodels

import replanets.common.TurnId
import replanets.ui.{Event, MapObject}

sealed trait BuildShipViewColumn
case object Hulls extends BuildShipViewColumn
case object Engines extends BuildShipViewColumn
case object Beams extends BuildShipViewColumn
case object Launchers extends BuildShipViewColumn

sealed trait CurrentView

object CurrentView {

  case object Map extends CurrentView

  case object BuildShip extends CurrentView

}

case class ViewModel (
  var turnShown: TurnId,
  private var _selectedObject: Option[MapObject]
) {

  private var _currentView: CurrentView = CurrentView.Map

  def currentView_=(value: CurrentView): Unit = {
    _currentView = value
    currentViewChanged.fire()
  }

  def currentView = _currentView

  def selectedObject_=(value: Option[MapObject]): Unit = {
    _selectedObject = value
    selectedObjectChaged.fire()
  }

  def selectedObject = _selectedObject

  val selectedObjectChaged = Event[Unit]
  val objectChanged = Event[MapObject]
  val currentViewChanged = Event[Unit]

  objectChanged += { mo =>
    selectedObject.foreach { so =>
      if (mo.getClass == so.getClass && mo.id == so.id && mo != so) {
        selectedObject = Some(mo)
      }
    }
  }
}