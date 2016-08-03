package replanets.ui

import replanets.model.Game

import scalafx.Includes._
import scalafx.scene.Node
import scalafx.scene.canvas.Canvas
import scalafx.scene.input.{MouseButton, MouseDragEvent, MouseEvent}
import scalafx.scene.layout.Pane
import scalafx.scene.paint.Color
import scalafx.scene.shape.Circle

case class Coords(x: Double, y: Double)

class MapView(game: Game) extends Pane {
  self: Node =>

  var scale = 0.2
  var offsetX = -100d
  var offsetY = -100d
  val scaleStep = 1.5

  var movingStartedPoint = (0d, 0d)
  var initialOffset = (0d, 0d)
  var movingDelta = (0d, 0d)

  style = "-fx-background-color: darkblue;"

  val canvas = new Canvas {
    height <== self.height
    width <== self.width

    onMousePressed = (e: MouseEvent) => {
      if(e.button == MouseButton.Primary) {
        movingStartedPoint = (e.x, e.y)
        initialOffset = (offsetX, offsetY)
      }
    }

    onMouseDragged = (e: MouseEvent) => {
      if(e.primaryButtonDown) {
        movingDelta = (e.x - movingStartedPoint._1, e.y - movingStartedPoint._2)
        offsetX = initialOffset._1 + movingDelta._1
        offsetY = initialOffset._2 + movingDelta._2
        redraw()
      }
    }
  }

  children = Seq(canvas)

  redraw()


  def zoom(scaleStep: Double, zoomPoint: Coords) = {
    val mapCoordsZoomPoint = Coords((zoomPoint.x - offsetX) / scale, (zoomPoint.y - offsetY) / scale)

    scale = scale * scaleStep
    offsetX = zoomPoint.x - mapCoordsZoomPoint.x * scale
    offsetY = zoomPoint.y - mapCoordsZoomPoint.y * scale

    redraw()
  }

  def zoomIn(): Unit = {
    zoom(scaleStep, Coords(width.toDouble/2, height.toDouble/2))
  }

  def zoomOut(): Unit = {
    zoom(1/scaleStep, Coords(width.toDouble/2, height.toDouble/2))
  }

  def redraw(): Unit = {
    val gc = canvas.graphicsContext2D

    gc.clearRect(0, 0, width.toDouble, height.toDouble)

    game.map.planets.foreach { p =>
      gc.setFill(Color.Wheat)
      gc.fillOval(p.x * scale + offsetX, p.y * scale + offsetY, 4, 4)
    }
  }
}
