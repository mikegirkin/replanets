package replanets.ui

import replanets.common._
import replanets.model
import replanets.model.{Game, TurnInfo}

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

  case class OwnShip(id: Int, coords: IntCoords, displayName: String, ship: replanets.common.OwnShip) extends MapObject
  case class Target(id: Int, coords: IntCoords, displayName: String, target: replanets.common.Target) extends MapObject
  case class Contact(id: Int, coords: IntCoords, displayName: String, contact: replanets.common.Contact) extends MapObject
  case class Planet(id: Int, coords: IntCoords, displayName: String) extends MapObject
  case class Starbase(id: Int, coords: IntCoords, displayName: String) extends MapObject
  case class Minefield(id: Int, coords: IntCoords, displayName: String) extends MapObject
  case class Explosion(id: Int, coords: IntCoords, displayName: String) extends MapObject
  case class IonStorm(id: Int, coords: IntCoords, displayName: String) extends MapObject
  case class Other(id: Int, coords: IntCoords, displayName: String) extends MapObject

  def forShip(ship: Ship): MapObject = {
    ship match {
      case x: replanets.common.OwnShip => MapObject.OwnShip(ship.id.value, ship.coords, x.name, x)
      case x: replanets.common.Target => MapObject.Target(ship.id.value, ship.coords, x.hull.name, x)
      case x: replanets.common.Contact => MapObject.Contact(ship.id.value, ship.coords, s"Ship ${ship.id}", x)
    }
  }

  def forStarbase(base: model.Starbase): MapObject = {
    val planet = base.planet.mapData
    MapObject.Starbase(base.id.value, planet.coords, s"Starbase ${base.id.value}")
  }

  def forPlanet(planet: replanets.model.PlanetMapData) = {
    MapObject.Planet(planet.id, planet.coords, planet.name)
  }

  def forPlanet(planet: replanets.common.Planet) = {
    MapObject.Planet(planet.id.value, planet.mapData.coords, planet.mapData.name)
  }

  def forIonStorm(storm: replanets.common.IonStorm) = {
    MapObject.IonStorm(storm.id, storm.coords, s"Ion storm ${storm.id}")
  }

  def forMinefield(game: Game)(minefield: replanets.common.MineFieldRecord) = {
    MapObject.Minefield(minefield.id, minefield.coords,
      if(minefield.owner < 12) s"${game.races(minefield.owner - 1).adjective} minefield ${minefield.id}"
      else if(minefield.owner == 12) s"${game.races(6).adjective} web minefield ${minefield.id}"
      else "Unknown minefield")
  }

  def forExplosion(explosion: replanets.common.ExplosionRecord) = {
    MapObject.Explosion(explosion.id, explosion.coords, s"Explosion ${explosion.id}")
  }

  def findAtCoords(game: Game, turn: TurnId)(coords: IntCoords): IndexedSeq[MapObject] = {
    //ships
    val ships = shipsAtCoords(game, turn)(coords)
    //planets
    val planets = game.specs.map.planets
      .find(p => p.coords == coords)
      .map(p => forPlanet(p))
      .toIndexedSeq
    //bases
    val bases = planets.flatMap { p =>
      game.turnSeverData(turn).bases.get(PlanetId(p.id))
    }.map { b => forStarbase(b) }
      .toIndexedSeq
    //mine fields
    val mineFields = game.turnSeverData(turn).mineFields
      .filter(mf => mf.coords == coords)
      .map(mf => MapObject.forMinefield(game)(mf))
    //ion storms
    val ionStorms = game.turnSeverData(turn).ionStorms
      .filter(is => is.coords == coords)
      .map(is => MapObject.forIonStorm(is))
    //explosions
    val explosions = game.turnSeverData(turn).explosions
      .filter(ex => ex.coords == coords)
      .map(ex => MapObject.forExplosion(ex))

    planets ++ bases ++ ships ++ mineFields ++ explosions ++ ionStorms
  }

  def shipsAtCoords(game: Game, turn: TurnId)(coords: IntCoords): Iterable[MapObject] = {
    game.turnSeverData(turn).ships.values
      .filter(ship => ship.coords == coords)
      .map { ship => forShip(ship) }
  }

}


