package replanets.model.commands.v0

import replanets.common._
import replanets.model.{Cargo, Specs}

case class ShipToOwnPlanetTransfer(
  shipId: ShipId,
  planetId: PlanetId,
  transfer: Cargo
) extends PlayerCommand {

  override def isReplacableBy(other: PlayerCommand): Boolean = {
    other match {
      case ShipToOwnPlanetTransfer(newObjectId, _, _) => this.shipId == newObjectId
      case _ => false
    }
  }

  override def isAddDiffToInitialState(initial: GameState, specs: Specs): Boolean = {
    transfer != Cargo.zero
  }

  override def apply(state: GameState, specs: Specs): GameState = {
    val planet = state.planets(planetId)
    val newState = planet.copy(
      surfaceMinerals = planet.surfaceMinerals.copy(
        neutronium = planet.surfaceMinerals.neutronium + transfer.neu,
        tritanium = planet.surfaceMinerals.tritanium + transfer.tri,
        duranium = planet.surfaceMinerals.duranium + transfer.dur,
        molybdenium = planet.surfaceMinerals.molybdenium + transfer.mol
      ),
      supplies = planet.supplies + transfer.supplies,
      colonistClans = planet.colonistClans + transfer.colonists,
      money = planet.money + transfer.money
    )
    val ship = state.ownShips(shipId)
    val torpsType = ship.torpsType
    val starbaseState = for(
      torps <- torpsType;
      starbase <- state.bases.get(planetId);
      newTorpsNumber = starbase.storedTorpedoes(torps.id.value - 1) + transfer.torps
    ) yield starbase.copy(
      storedTorpedoes = starbase.storedTorpedoes.updated(torps.id.value - 1, newTorpsNumber)
    )
    val shipState = ship.copy(
      minerals = ship.minerals.minus(transfer.minerals),
      supplies = ship.supplies - transfer.supplies,
      colonistClans = ship.colonistClans - transfer.colonists,
      money = ship.money - transfer.money,
      torpsFightersLoaded = ship.torpsFightersLoaded - transfer.torps
    )
    val planets = state.planets.updated(newState.id, newState)
    val bases = starbaseState.map { s =>
      state.bases.updated(s.id, s)
    }.getOrElse(state.bases)
    state.copy(
      planets = planets,
      bases = bases,
      ships = state.ships.updated(shipState.id, shipState)
    )
  }

  override def mergeWith(newerCommand: PlayerCommand): PlayerCommand = {
    newerCommand match {
      case ShipToOwnPlanetTransfer(newObjectId, newPlanetId, newTransfer) if this.shipId == newObjectId && this.planetId == newPlanetId =>
        this.copy(
          transfer = transfer.plus(newTransfer)
        )
      case _ => this
    }
  }
}