package replanets.model.commands

import replanets.common.{PlanetId, ServerData, ShipId}
import replanets.model.{Cargo, Specs}

case class ShipToPlanetTransfer(
  objectId: ShipId,
  planetId: PlanetId,
  transfer: Cargo
) extends PlayerCommand {

  override def isReplacableBy(other: PlayerCommand): Boolean = {
    other match {
      case ShipToPlanetTransfer(newObjectId, newPlanetId, newTransfer) if this.objectId == newObjectId && this.planetId == newPlanetId => true
      case _ => false
    }
  }

  override def isAddDiffToInitialState(initial: ServerData, specs: Specs): Boolean = {
    if(transfer.neu == 0
      && transfer.tri == 0
      && transfer.dur == 0
      && transfer.mol == 0
      && transfer.supplies == 0
      && transfer.colonists == 0
      && transfer.money == 0
      && transfer.torps == 0) false
    else true
  }

  override def apply(state: ServerData, specs: Specs): ServerData = {
    val planetState = state.planets.get(planetId).map { p =>
      p.copy(
        surfaceMinerals = p.surfaceMinerals.copy(
          neutronium = p.surfaceMinerals.neutronium + transfer.neu,
          tritanium = p.surfaceMinerals.tritanium + transfer.tri,
          duranium = p.surfaceMinerals.duranium + transfer.dur,
          molybdenium = p.surfaceMinerals.molybdenium + transfer.mol
        ),
        supplies = p.supplies + transfer.supplies,
        colonistClans = p.colonistClans + transfer.colonists,
        money = p.money + transfer.money
      )
    }
    val ship = state.ownShips(objectId)
    val torpsType = ship.torpsType
    val starbaseState = for(
      torps <- torpsType;
      starbase <- state.bases.get(planetId);
      newTorpsNumber = starbase.storedTorpedoes(torps.id.value - 1) + transfer.torps
    ) yield starbase.copy(
      storedTorpedoes = starbase.storedTorpedoes.updated(torps.id.value - 1, newTorpsNumber)
    )
    val shipState = ship.copy(
      minerals = ship.minerals.copy(
        neutronium = ship.minerals.neutronium - transfer.neu,
        tritanium = ship.minerals.tritanium - transfer.tri,
        duranium = ship.minerals.duranium - transfer.dur,
        molybdenium = ship.minerals.molybdenium - transfer.mol
      ),
      supplies = ship.supplies - transfer.supplies,
      colonistClans = ship.colonistClans - transfer.colonists,
      money = ship.money - transfer.money,
      torpsFightersLoaded = ship.torpsFightersLoaded - transfer.torps
    )
    val planets = planetState.map { p =>
      state.planets.updated(p.id, p)
    }.getOrElse(state.planets)
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
      case ShipToPlanetTransfer(newObjectId, newPlanetId, newTransfer) if this.objectId == newObjectId && this.planetId == newPlanetId =>
        if(newTransfer.neu != 0) this.copy(transfer = this.transfer.copy(neu = this.transfer.neu + newTransfer.neu))
        else if(newTransfer.tri != 0) this.copy(transfer = this.transfer.copy(tri = this.transfer.tri + newTransfer.tri))
        else if(newTransfer.dur != 0) this.copy(transfer = this.transfer.copy(dur = this.transfer.dur + newTransfer.dur))
        else if(newTransfer.mol != 0) this.copy(transfer = this.transfer.copy(mol = this.transfer.mol + newTransfer.mol))
        else if(newTransfer.supplies != 0) this.copy(transfer = this.transfer.copy(supplies = this.transfer.supplies + newTransfer.supplies))
        else if(newTransfer.colonists != 0) this.copy(transfer = this.transfer.copy(colonists = this.transfer.colonists + newTransfer.colonists))
        else if(newTransfer.money != 0) this.copy(transfer = this.transfer.copy(money = this.transfer.money + newTransfer.money))
        else if(newTransfer.torps != 0) this.copy(transfer = this.transfer.copy(torps = this.transfer.torps + newTransfer.torps))
        else this
      case _ => this
    }
  }
}
