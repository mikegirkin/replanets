package replanets.ui

import replanets.common._
import replanets.model.Game
import replanets.ui.viewmodels.ViewModel

import scalafx.Includes._
import scalafx.scene.Node
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.input.{MouseButton, MouseEvent}
import scalafx.scene.layout.Pane
import scalafx.scene.paint.Color

class MapView(game: Game, viewModel: ViewModel) extends Pane {
  self: Node =>

  import DoubleExtensions._
  import GraphicContextExtensions._

  val ownShipColor = Color.LimeGreen
  val enemyShipColor = Color.Red
  val mixedShipsColor = Color.Yellow
  val contactShipColor = Color.OrangeRed

  val enemyMineFieldColor = Color.Purple
  val ownMineFieldColor = Color.Aqua

  val unscannedPlanetColor = Color.LightGray
  val explosionColor = Color.Purple

  var scale = 0.4
  var offsetX = -350d
  var offsetY = -350d
  val scaleStep = 1.5

  def planetDiameter: Double =
    if (scale < 0.2) 2
    else if(scale < 0.6) 3
    else if (scale < 1.4) 5
    else 7

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
        val objectsAtPoint = MapObject.findAtCoords(game, viewModel.turnShown)(closestObject.coords)
        selectMapObject(objectsAtPoint(0))
      }
    }
  }

  children = Seq(canvas)

  redraw()

  private def currentTurn = game.turns(viewModel.turnShown)
  private def currentTurnServerData = currentTurn(game.playingRace).initialState

  private def closestObjectTo(coords: IntCoords): MapObject = {
    //planets
    val closestPlanet = game.specs.map.planets.minBy(x => distSqr(coords, x.coords))
    //ships
    val closestShip = if(currentTurnServerData.ships.isEmpty) None else Some(currentTurnServerData.ships.values.minBy(x => distSqr(coords, x.coords)))
    //minefields
    val closestMinefield = if(currentTurnServerData.mineFields.isEmpty) None else Some(currentTurnServerData.mineFields.minBy(x => distSqr(coords, x.coords)))
    //explosions
    val closestExplosion = if(currentTurnServerData.explosions.isEmpty) None else Some(currentTurnServerData.explosions.minBy(x => distSqr(coords, x.coords)))
    //ionstroms
    val closestStorm = if(currentTurnServerData.ionStorms.isEmpty) None else Some(currentTurnServerData.ionStorms.minBy(s => distSqr(coords, s.coords)))

    (Seq(MapObject.forPlanet(closestPlanet)) ++
      closestShip.map(x => MapObject.forShip(x)) ++
      closestMinefield.map(x => MapObject.forMinefield(game)(x)) ++
      closestStorm.map(x => MapObject.forIonStorm(x)) ++
      closestExplosion.map(x => MapObject.forExplosion(x))
    ).minBy(x => distSqr(coords, x.coords))
  }

  private def selectMapObject(it: MapObject): Unit = {
    viewModel.selectedObject = Some(it)
    redraw()
  }

  private def zoom(scaleStep: Double, zoomPoint: Coords) = {
    val mapCoordsZoomPoint = mapCoords(zoomPoint)

    scale = scale * scaleStep
    offsetX = zoomPoint.x - mapCoordsZoomPoint.x * scale
    offsetY = zoomPoint.y - (Constants.MapHeight - mapCoordsZoomPoint.y) * scale

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
    drawIonStorms(gc)
    drawMineFields(gc)
    drawShips(gc)
    drawPlanets(gc)
    drawExplosions(gc)
    drawSelectedCross(gc)
  }

  private def drawMineFields(gc: GraphicsContext) = {
    currentTurnServerData.mineFields.foreach { m =>
      val color = if(m.owner == game.playingRace.value) ownMineFieldColor else enemyMineFieldColor
      gc.setStroke(color)
      val coords = canvasCoord(IntCoords(m.x, m.y))
      val canvasRadius = m.radius * scale
      gc.strokeCircle(coords, canvasRadius * 2)
    }
  }

  private def drawSelectedCross(gc: GraphicsContext) = {
    viewModel.selectedObject.foreach { it =>
      val graphicCoords = canvasCoord(it.coords)
      gc.setStroke(Color.White)
      gc.setLineWidth(1)
      gc.strokeLine(graphicCoords.x - crossSize/2, graphicCoords.y, graphicCoords.x + crossSize/2, graphicCoords.y)
      gc.strokeLine(graphicCoords.x, graphicCoords.y - crossSize/2, graphicCoords.x, graphicCoords.y + crossSize/2)
    }
  }

  private def drawPlanets(gc: GraphicsContext) = {
    def planetColor(owner: Option[RaceId], hasBase: Boolean): Color = {
      owner.fold(unscannedPlanetColor)( ow =>
        if(ow == game.playingRace) if(hasBase) Color.Aqua else Color.Green
        else if(ow.value == 0) Color.Yellow
        else if(hasBase) Color.Red else Color.OrangeRed
      )
    }

    (1 to 500).foreach { idx =>
      val planetCoords = game.specs.map.planets(idx - 1).coords
      val planetInfo = currentTurnServerData.planets.get(PlanetId(idx))
      val baseInfo = planetInfo.flatMap { p => currentTurnServerData.bases.get(p.id) }

      val coord = canvasCoord(planetCoords)
      gc.setFill(planetColor(planetInfo.map(_.owner), baseInfo.isDefined))
      gc.fillOval(coord.x - planetDiameter/2, coord.y - planetDiameter/2, planetDiameter, planetDiameter)
    }
  }

  private def drawShips(gc: GraphicsContext) = {
    val shipCircleSize = planetDiameter / 2

    val ships = game.turnSeverData(viewModel.turnShown).ships

    val shipsByCoords = ships.values.groupBy(_.coords)

    shipsByCoords.foreach { case (coords, localShips) =>
      if(game.specs.map.planets.exists(_.coords == coords)) {
        drawShipsOrbitingPlanets(coords, localShips.toSeq)
      } else {
        drawShipsNotOrbitingPlanet(coords, localShips.toSeq)
      }
    }

    def drawShipsOrbitingPlanets(coords: IntCoords, ships: Seq[Ship]) = {
      val beginCoords = canvasCoord(coords)
      for(
        ship <- ships;
        destination <- ship.plannedDestination
      ) {
        val destinationCoords = canvasCoord(destination)
        gc.setStroke(getColor(ship))
        gc.strokeLine(beginCoords.x, beginCoords.y, destinationCoords.x, destinationCoords.y)
      }
      gc.setStroke(getColor(ships:_*))
      gc.strokeCircle(beginCoords, shipCircleDiameter)
    }

    def drawShipsNotOrbitingPlanet(coords: IntCoords, ships: Seq[Ship]) = {
      val beginCoords = canvasCoord(coords)
      for(
        ship <- ships;
        destination <- ship.plannedDestination
      ) {
        val destinationCoords = canvasCoord(destination)
        gc.setStroke(getColor(ship))
        gc.strokeLine(beginCoords.x, beginCoords.y, destinationCoords.x, destinationCoords.y)
      }
      val color = getColor(ships:_*)
      gc.setStroke(color)
      gc.setFill(color)
      gc.fillCircle(beginCoords, planetDiameter/2)
    }

    def getColor(localShips: Ship*) = {
      def colorOf(ship: Ship) = {
        ship match {
          case _: OwnShip => ownShipColor
          case _: Target => enemyShipColor
          case _: Contact => contactShipColor
        }
      }
      localShips.map{ s => colorOf(s) }.reduce((c1, c2) => {
        if(c1 == c2) c1 else mixedShipsColor
      })
    }
  }

  private def drawIonStorms(gc: GraphicsContext) = {
    val storms = currentTurnServerData.ionStorms
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

  private def drawExplosions(gc: GraphicsContext) = {
    val explosions = currentTurnServerData.explosions
    for(e <- explosions) {
      val coords = canvasCoord(Coords(e.x, e.y))
      val size = planetDiameter / 2 + 1

      gc.setStroke(explosionColor)
      gc.strokeLine(coords.x - size, coords.y - size, coords.x + size, coords.y + size)
      gc.strokeLine(coords.x + size, coords.y - size, coords.x - size, coords.y + size)
    }
  }

  private def drawMovementVector(mapCoords: Coords, heading: Int, warp: Int)(gc: GraphicsContext): Unit = {
    val dx = warp * warp * Math.sin(2 * Math.PI * heading / 360)
    val dy = warp * warp * Math.cos(2 * Math.PI * heading / 360)
    if(dx != 0 || dy != 0) drawMovementVector(mapCoords, Coords(mapCoords.x + dx, mapCoords.y + dy))(gc)
  }

  private def drawMovementVector(mapCoords: Coords, mapWaypoint: Coords)(gc: GraphicsContext): Unit = {
    gc.setStroke(Color.Purple)
    gc.setLineWidth(1)
    val startPoint = canvasCoord(mapCoords)
    val endPoint = canvasCoord(mapWaypoint)
    if(!startPoint.x.almostEqual(endPoint.x, 0.001) || !startPoint.y.almostEqual(endPoint.y, 0.001))
      gc.strokeLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y)
  }

}
