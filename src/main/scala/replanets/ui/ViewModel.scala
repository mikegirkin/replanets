package replanets.ui

case class Coords(x: Double, y: Double) {
  def shift(dx: Double, dy: Double) = {
    Coords(x + dx, y + dy)
  }
}

case class IntCoords(x: Int, y: Int)

sealed trait MapObjectType {}
object MapObjectType {
  case object Ship extends MapObjectType
  case object Base extends MapObjectType
  case object Planet extends MapObjectType
  case object IonStorm extends MapObjectType
}

case class MapObject(
  objectType: MapObjectType,
  id: Int,
  coords: IntCoords
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
