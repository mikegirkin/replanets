package replanets.ui

case class Coords(x: Double, y: Double)

sealed trait MapObjectType {}
object MapObjectType {
  case object Ship extends MapObjectType
  case object Planet extends MapObjectType
  case object IonStorm extends MapObjectType
}

case class MapObject(
  objectType: MapObjectType,
  id: Int,
  coords: Coords
)

case class ViewModel (
  var turnShown: Int,
  private var _objectSelected: Option[MapObject]
) {

  def objectSelected_=(value: Option[MapObject]): Unit = {
    _objectSelected = value
    selectedObjectChaged.fire()
  }

  def objectSelected = _objectSelected

  val selectedObjectChaged = new Event()
}
