package replanets.ui

import replanets.common._
import replanets.model.{Game, PlanetId}

case class Coords(x: Double, y: Double) {
  def shift(dx: Double, dy: Double) = {
    Coords(x + dx, y + dy)
  }
}

trait MapObject {
  val id: Int
  val coords: IntCoords
  val displayName: String
}

object MapObject {

  case class OwnShip(id: Int, coords: IntCoords, displayName: String) extends MapObject
  case class Target(id: Int, coords: IntCoords, displayName: String) extends MapObject
  case class Contact(id: Int, coords: IntCoords, displayName: String) extends MapObject
  case class Planet(id: Int, coords: IntCoords, displayName: String) extends MapObject
  case class Starbase(id: Int, coords: IntCoords, displayName: String) extends MapObject
  case class Minefield(id: Int, coords: IntCoords, displayName: String) extends MapObject
  case class Explosion(id: Int, coords: IntCoords, displayName: String) extends MapObject
  case class IonStorm(id: Int, coords: IntCoords, displayName: String) extends MapObject
  case class Other(id: Int, coords: IntCoords, displayName: String) extends MapObject

  def forShip(ship: Ship): MapObject = {
    ship match {
      case x: replanets.common.OwnShip => MapObject.OwnShip(ship.id.value, ship.coords, x.name)
      case x: replanets.common.Target => MapObject.Target(ship.id.value, ship.coords, x.hull.name)
      case x: replanets.common.Contact => MapObject.Contact(ship.id.value, ship.coords, s"Ship ${ship.id}")
    }
  }

  def forStarbase(game: Game)(base: BaseRecord): MapObject = {
    val coords = game.map.planets.find(p => p.id == base.baseId).get.coords
    MapObject.Starbase(base.baseId, coords, s"Starbase ${base.baseId}")
  }

  def forPlanet(planet: replanets.model.Planet) = {
    MapObject.Planet(planet.id, planet.coords, planet.name)
  }

  def forIonStorm(storm: replanets.common.IonStorm) = {
    MapObject.IonStorm(storm.id, storm.coords, s"Ion storm ${storm.id}")
  }

  def findAtCoords(game: Game, turn: TurnId)(coords: IntCoords): IndexedSeq[MapObject] = {
    //ships
    val ships = game.turnSeverData(turn).ships.values
      .filter(ship => ship.coords == coords)
      .map { ship => forShip(ship) }
    //planets
    val planets = game.map.planets
      .find(p => p.coords == coords)
      .map(p => forPlanet(p))
      .toIndexedSeq
    //bases
    val bases = game.turnSeverData(turn).bases
      .find(b => {
        (for(
          planet <- game.map.planets.find(_.id == b.baseId)
        ) yield planet.coords == coords)
          .getOrElse(false)
      })
      .map(b => MapObject.forStarbase(game)(b))
      .toIndexedSeq
    //mine fields
    val mineFields = game.turnSeverData(turn).mineFields
      .filter(mf => mf.coords == coords)
      .map(mf => MapObject.Minefield(mf.id, coords, s"${game.races(mf.owner - 1).adjective} minefield ${mf.id}"))
    //ion storms
    val ionStorms = game.turnSeverData(turn).ionStorms
      .filter(is => is.coords == coords)
      .map(is => MapObject.forIonStorm(is))
    //explosions
    val explosions = game.turnSeverData(turn).explosions
      .filter(ex => ex.coords == coords)
      .map(ex => MapObject.Explosion(ex.id, coords, s"Explosion ${ex.id}"))

    planets ++ bases ++ ships ++ mineFields ++ explosions ++ ionStorms
  }
}


