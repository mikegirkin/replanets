package replanets.ui

import scalafx.scene.canvas.GraphicsContext

/**
  * Created by mgirkin on 10/08/2016.
  */
object GraphicContextExtensions {
  implicit class GraphicsContextExt(val gc: GraphicsContext) extends AnyVal {
    def fillCircle(center: Coords, diameter: Double) = {
      gc.fillOval(center.x - diameter/2, center.y - diameter/2, diameter, diameter)
    }

    def strokeCircle(center: Coords, diameter: Double) = {
      gc.strokeOval(center.x - diameter/2, center.y - diameter/2, diameter, diameter)
    }
  }
}
