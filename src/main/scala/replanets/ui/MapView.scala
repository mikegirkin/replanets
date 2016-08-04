package replanets.ui

import replanets.common.ShipCoordsRecord
import replanets.model.Game

import scalafx.Includes._
import scalafx.scene.Node
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.input.{MouseButton, MouseEvent}
import scalafx.scene.layout.Pane
import scalafx.scene.paint.Color

case class Coords(x: Double, y: Double)

class MapView(game: Game) extends Pane {
  self: Node =>

  var scale = 0.2
  var offsetX = -100d
  var offsetY = -100d
  val scaleStep = 1.5

  def planetSize: Double =
    if (scale < 0.2) 2
    else if (scale < 0.5) 5
    else 8

  def shipCircleSize: Double =
    planetSize + 4

  var movingStartedPoint = (0d, 0d)
  var initialOffset = (0d, 0d)
  var movingDelta = (0d, 0d)

  style = "-fx-background-color: black;"

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

  def canvasCoord(mapCoord: Coords): Coords = {
    Coords(mapCoord.x * scale + offsetX, mapCoord.y * scale + offsetY)
  }

  def canvasCoord(x: Int, y: Int): Coords = canvasCoord(Coords(x, y))

  def redraw(): Unit = {
    val gc = canvas.graphicsContext2D

    gc.clearRect(0, 0, width.toDouble, height.toDouble)
    drawPlanets(gc)
  }

  private def drawPlanets(gc: GraphicsContext) = {
    def planetColor(owner: Option[Int], hasBase: Boolean): Color = {
      owner.fold(Color.Wheat)( ow =>
        if(ow == game.playingRace)  if(hasBase) Color.Aquamarine else Color.LightGreen
        else if(hasBase) Color.Red else Color.OrangeRed
      )
    }

    def shipsOrbitingPlanet(planetCoords: Coords): Seq[ShipCoordsRecord] =
      game.turns.last.serverReceiveState.rstFiles(game.playingRace).shipCoords
        .filter(sc => sc.x == planetCoords.x && sc.y == planetCoords.y)

    (0 until 500).foreach { idx =>
      val planetCoords = Coords(game.map.planets(idx).x, game.map.planets(idx).y)
      val planetInfo = game.turns.last.serverReceiveState.rstFiles(game.playingRace).planets.find(_.planetId == idx + 1)
      val baseInfo = game.turns.last.serverReceiveState.rstFiles(game.playingRace).bases.find(_.baseId == idx + 1)

      val coord = canvasCoord(planetCoords)
      gc.setFill(planetColor(planetInfo.map(_.ownerId), baseInfo.isDefined))
      gc.fillOval(coord.x - planetSize/2, coord.y - planetSize/2, planetSize, planetSize)

      val orbitingShips = shipsOrbitingPlanet(planetCoords)
      if(orbitingShips.nonEmpty) {
        val color =
          if(orbitingShips.forall(_.owner == game.playingRace)) Color.MediumPurple
          else if(orbitingShips.forall(_.owner == game.playingRace)) Color.Red
          else Color.Orange
        gc.setStroke(color)
        gc.setLineWidth(2)
        gc.strokeOval(coord.x - shipCircleSize/2.0, coord.y - shipCircleSize/2.0, shipCircleSize, shipCircleSize)
      }
    }
  }

}
