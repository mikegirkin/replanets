package replanets.ui

import replanets.common.TurnId
import replanets.model.Game

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
  case object MineField extends MapObjectType
  case object Explosion extends MapObjectType
  case object Target extends MapObjectType
  case object Other extends MapObjectType
}

case class MapObject(
  objectType: MapObjectType,
  id: Int,
  coords: IntCoords
)

object MapObject {
  def findAtCoords(game: Game, turn: TurnId)(coords: IntCoords): IndexedSeq[(MapObject, String)] = {
    //ships
    val ships = game.turnSeverData(turn).ships
      .filter(ship => ship.coords == coords)
      .map(ship => (MapObject(MapObjectType.Ship, ship.shipId, coords), ship.name))
    //targets
    val contacts = game.turnSeverData(turn).targets
      .filter(t => t.coords == coords)
      .map(t => (MapObject(MapObjectType.Target, t.shipId, coords), t.name))
    //bases
    //planets
    //mine fields
    val mineFields = game.turnSeverData(turn).mineFields
      .filter(mf => mf.coords == coords)
      .map(mf => (MapObject(MapObjectType.MineField, mf.id, coords), s"${game.races(mf.owner - 1).adjective} minefield ${mf.id}"))
    //ion storms
    val ionStorms = game.turnSeverData(turn).ionStorms
      .filter(is => is.coords == coords)
      .map(is => (MapObject(MapObjectType.IonStorm, is.id, coords), s"Ion storm ${is.id}"))
    //explosions
    val explosions = game.turnSeverData(turn).explosions
      .filter(ex => ex.coords == coords)
      .map(ex => (MapObject(MapObjectType.Explosion, ex.id, coords), s"Explosion ${ex.id}"))

    ships ++ contacts ++ mineFields ++ explosions ++ ionStorms
  }
}


