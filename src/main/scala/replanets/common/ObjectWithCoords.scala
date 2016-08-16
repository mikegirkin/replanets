package replanets.common

trait ObjectWithCoords {
  val x: Short
  val y: Short

  def coords = IntCoords(x, y)
}
