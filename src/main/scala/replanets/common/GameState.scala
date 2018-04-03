package replanets.common

import replanets.model.Starbase

case class GameState(
  isWinplan: Boolean,
  subversion: String,

  ships: Map[ShipId, Ship],
  planets: Map[PlanetId, Planet],
  bases: Map[PlanetId, Starbase],
  messages: IndexedSeq[MessageInfo],
  generalInfo: GeneralTurnInformation,
  mineFields: IndexedSeq[MineFieldRecord],
  ionStorms: IndexedSeq[IonStorm],
  explosions: IndexedSeq[ExplosionRecord]
) {

  def ownShips: Map[ShipId, OwnShip] = ships.filter { case (_, ship) => ship.isInstanceOf[OwnShip] }.mapValues{ _.asInstanceOf[OwnShip] }

  def getShipsAtCoords(coords: IntCoords): Seq[Ship] = {
    ships.values
      .filter(ship => ship.coords == coords)
      .toSeq
  }

  def getOwnShipAtCoords(coords: IntCoords): Seq[OwnShip] = {
    getShipsAtCoords(coords)
      .filter(_.isInstanceOf[OwnShip])
  }
}
