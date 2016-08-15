package replanets.common

import replanets.ui.IntCoords

/**
  * Created by mgirkin on 15/08/2016.
  */
trait ObjectWithCoords {
  val x: Short
  val y: Short

  def coords = IntCoords(x, y)
}
