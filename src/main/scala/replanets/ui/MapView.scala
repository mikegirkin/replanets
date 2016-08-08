package replanets.ui

import replanets.common.{Constants, ShipCoordsRecord}
import replanets.model.Game

import scalafx.Includes._
import scalafx.scene.Node
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.input.{MouseButton, MouseEvent}
import scalafx.scene.layout.Pane
import scalafx.scene.paint.Color

class MapView(game: Game, viewModel: ViewModel) extends Pane {
  self: Node =>

  var scale = 0.4
  var offsetX = -350d
  var offsetY = -350d
  val scaleStep = 1.5

  def planetDiameter: Double =
    if (scale < 0.2) 2
    else if(scale < 0.4) 3
    else if (scale < 0.7) 5
    else 8

  def shipCircleDiameter: Double = planetDiameter + 4
  def shipCircleThickness: Double = 1

  def crossSize = planetDiameter + 8

  var movingStartedPoint = (0d, 0d)
  var initialOffset = (0d, 0d)
  var movingDelta = (0d, 0d)

  style = "-fx-background-color: black;"

  val canvas = new Canvas {
    height <== self.height
    width <== self.width

    width.onChange(redraw())
    height.onChange(redraw())

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

    onMouseClicked = (e: MouseEvent) => {
      if(e.button == MouseButton.Primary && e.isStillSincePress) {
        val coords = mapCoords(e.x, e.y)
        val closestObject = closestObjectTo(IntCoords(Math.round(coords.x).toInt, Math.round(coords.y).toInt))
        selectMapObject(closestObject)
      }
    }
  }

  children = Seq(canvas)

  redraw()

  private def currentTurn = game.turns(viewModel.turnShown)
  private def currentTurnServerData = currentTurn.rstFiles(game.playingRace)

  private def closestObjectTo(coords: IntCoords): MapObject = {
    //planets
    val closestPlanet = game.map.planets.reduce((p1, p2) => if(distSqr(coords, IntCoords(p1.x, p1.y)) < distSqr(coords, IntCoords(p2.x, p2.y))) p1 else p2)
    //ships
    //minefields
    //explosions
    //ionstroms
    val closestStorm = currentTurnServerData.ionStorms.reduce((is1, is2) => if(distSqr(coords, IntCoords(is1.x, is1.y)) < distSqr(coords, IntCoords(is2.x, is2.y))) is1 else is2)

    Seq(
      MapObject(MapObjectType.Planet, closestPlanet.id, IntCoords(closestPlanet.x, closestPlanet.y)),
      MapObject(MapObjectType.IonStorm, closestStorm.id, IntCoords(closestStorm.x, closestStorm.y))
    ).reduce((x1, x2) => if(distSqr(coords, x1.coords) < distSqr(coords, x2.coords)) x1 else x2)
  }

  private def selectMapObject(it: MapObject): Unit = {
    viewModel.objectSelected = Some(it)
    redraw()
  }


  private def zoom(scaleStep: Double, zoomPoint: Coords) = {
    val mapCoordsZoomPoint = mapCoords(zoomPoint)

    scale = scale * scaleStep
    offsetX = zoomPoint.x - mapCoordsZoomPoint.x * scale
    offsetY = zoomPoint.y - mapCoordsZoomPoint.y * scale

    redraw()
  }

  private def distSqr(point1: Coords, point2: Coords): Double =
    (point1.x - point2.x) * (point1.x - point2.x) + (point1.y - point2.y) * (point1.y - point2.y)

  private def distSqr(point1: IntCoords, point2: IntCoords): Double =
    (point1.x - point2.x) * (point1.x - point2.x) + (point1.y - point2.y) * (point1.y - point2.y)

  def zoomIn(): Unit = {
    zoom(scaleStep, Coords(width.toDouble/2, height.toDouble/2))
  }

  def zoomOut(): Unit = {
    zoom(1/scaleStep, Coords(width.toDouble/2, height.toDouble/2))
  }

  def canvasCoord(mapCoord: Coords): Coords = {
    Coords(mapCoord.x * scale + offsetX, (Constants.MapHeight - mapCoord.y) * scale + offsetY)
  }

  def canvasCoord(x: Double, y: Double): Coords = canvasCoord(Coords(x, y))

  def canvasCoord(mapCoord: IntCoords): Coords = {
    canvasCoord(mapCoord.x, mapCoord.y)
  }

  def mapCoords(canvasCoords: Coords): Coords = mapCoords(canvasCoords.x, canvasCoords.y)

  def mapCoords(canvasX: Double, canvasY: Double): Coords = Coords(
    (canvasX - offsetX) / scale,
    Constants.MapHeight - (canvasY - offsetY) / scale
  )


  def redraw(): Unit = {
    val gc = canvas.graphicsContext2D

    gc.clearRect(0, 0, width.toDouble, height.toDouble)
    drawPlanets(gc)
    drawIonStorms(gc)
    drawSelectedCross(gc)
  }

  private def drawSelectedCross(gc: GraphicsContext) = {
    viewModel.objectSelected.foreach { it =>
      val graphicCoords = canvasCoord(it.coords)
      gc.setStroke(Color.White)
      gc.setLineWidth(1)
      gc.strokeLine(graphicCoords.x - crossSize/2, graphicCoords.y, graphicCoords.x + crossSize/2, graphicCoords.y)
      gc.strokeLine(graphicCoords.x, graphicCoords.y - crossSize/2, graphicCoords.x, graphicCoords.y + crossSize/2)
    }
  }

  private def drawPlanets(gc: GraphicsContext) = {
    def planetColor(owner: Option[Int], hasBase: Boolean): Color = {
      owner.fold(Color.Wheat)( ow =>
        if(ow == game.playingRace)  if(hasBase) Color.Aquamarine else Color.LightGreen
        else if(hasBase) Color.Red else Color.OrangeRed
      )
    }

    def shipsOrbitingPlanet(planetCoords: Coords): Seq[ShipCoordsRecord] =
      game.turns(viewModel.turnShown).rstFiles(game.playingRace).shipCoords
        .filter(sc => sc.x == planetCoords.x && sc.y == planetCoords.y)

    (0 until 500).foreach { idx =>
      val planetCoords = Coords(game.map.planets(idx).x, game.map.planets(idx).y)
      val planetInfo = game.turns(viewModel.turnShown).rstFiles(game.playingRace).planets.find(_.planetId == idx + 1)
      val baseInfo = game.turns(viewModel.turnShown).rstFiles(game.playingRace).bases.find(_.baseId == idx + 1)

      val coord = canvasCoord(planetCoords)
      gc.setFill(planetColor(planetInfo.map(_.ownerId), baseInfo.isDefined))
      gc.fillOval(coord.x - planetDiameter/2, coord.y - planetDiameter/2, planetDiameter, planetDiameter)

      val orbitingShips = shipsOrbitingPlanet(planetCoords)
      if(orbitingShips.nonEmpty) {
        val color =
          if(orbitingShips.forall(_.owner == game.playingRace)) Color.MediumPurple
          else if(orbitingShips.forall(_.owner == game.playingRace)) Color.Red
          else Color.Orange
        gc.setStroke(color)
        gc.setLineWidth(shipCircleThickness)
        gc.strokeOval(coord.x - shipCircleDiameter/2.0, coord.y - shipCircleDiameter/2.0, shipCircleDiameter, shipCircleDiameter)
      }
    }
  }

  private def drawIonStorms(gc: GraphicsContext) = {
    val storms = game.turns(viewModel.turnShown).rstFiles(game.playingRace).ionStorms
    storms.filter(_.category > 0).foreach { s =>
      val color = s.category match {
        case 1 => Color.LightGreen
        case 2 => Color.Green
        case 3 => Color.Yellow
        case 4 => Color.Orange
        case 5 => Color.Red
      }
      gc.setStroke(color)
      gc.setLineWidth(1)
      val mapCoords = Coords(s.x, s.y)
      val crds = canvasCoord(s.x, s.y)
      val canvasRadius = s.radius * scale
      gc.strokeOval(crds.x - canvasRadius, crds.y - canvasRadius, canvasRadius * 2, canvasRadius * 2)
      drawMovementVector(mapCoords, s.heading, s.warp)(gc)
    }
  }

  private def drawMovementVector(mapCoords: Coords, heading: Int, warp: Int)(gc: GraphicsContext) = {
    gc.setStroke(Color.Purple)
    gc.setLineWidth(1)
    val startPoint = canvasCoord(mapCoords)
    val dx = warp * warp * Math.sin(2 * Math.PI * heading / 360)
    val dy = warp * warp * Math.cos(2 * Math.PI * heading / 360)
    val endPoint = canvasCoord(mapCoords.shift(dx, dy))
    gc.strokeLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y)
  }

}
