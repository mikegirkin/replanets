package replanets.model

import replanets.common._

import scala.collection.mutable

case class TurnInfo(
  initialState: ServerData,
  commands: mutable.Buffer[PlayerCommand]
) {

  def stateAfterCommands(specs: Specs): ServerData = {
    commands.foldLeft(initialState)((state, command) => {
      command match {
        case x:SetPlanetFcode => handle(x)(state)
        case x:SetShipFcode => handle(x)(state)
        case x:StartShipConstruction => handle(x, specs)(state)
        case x:StopShipConstruction => handle(x)(state)
        case _ => state
      }
    })
  }

  private def handle(command: SetPlanetFcode)(state: ServerData): ServerData = {
    state.copy(
      planets = state.planets.updated(command.objectId, state.planets(command.objectId).copy(fcode = command.newFcode))
    )
  }

  private def handle(command: SetShipFcode)(state: ServerData): ServerData = {
    val ship = state.ships(command.objectId)
    if(!ship.isInstanceOf[OwnShip]) state
    else {
      val ownShip = ship.asInstanceOf[OwnShip]
      state.copy(
        ships = state.ships.updated(command.objectId, ownShip.copy(fcode = command.newFcode))
      )
    }
  }

  private def handle(command: StartShipConstruction, specs: Specs)(state: ServerData): ServerData = {
    val order = command.getBuildOrder(specs)
    val base = state.bases(command.objectId)
    val hullTechLevel = Math.max(order.hull.techLevel, base.hullsTech)
    val engineTechLevel = Math.max(order.engine.techLevel, base.engineTech)
    val beamTechLevel = Math.max(order.beams.map(_.spec.techLevel).getOrElse(1), base.beamTech)
    val torpsTechLevel = Math.max(order.launchers.map(_.spec.techLevel).getOrElse(1), base.torpedoTech)
    val cost = base.shipCostAtStarbase(order)
    val planet = state.planets(command.objectId)
    val newPlanetMoney = if(planet.money >= cost.total.money) planet.money - cost.total.money else 0
    val newPlanetSupplies = {
      if(planet.money >= cost.total.money) planet.supplies
      else planet.supplies - (cost.total.money - planet.money)
    }
    state.copy(
      bases = state.bases.updated(command.objectId, base.copy(
        shipBeingBuilt = Some(command.getBuildOrder(specs)),
        hullsTech = hullTechLevel,
        engineTech = engineTechLevel,
        beamTech = beamTechLevel,
        torpedoTech = torpsTechLevel
      )),
      planets = state.planets.updated(command.objectId, planet.copy(
        surfaceMinerals = planet.surfaceMinerals.copy(
          tritanium = planet.surfaceMinerals.tritanium - cost.total.tri,
          duranium = planet.surfaceMinerals.duranium - cost.total.dur,
          molybdenium = planet.surfaceMinerals.molybdenium - cost.total.mol
        ),
        money = newPlanetMoney,
        supplies = newPlanetSupplies
      ))
    )
  }

  private def handle(command: StopShipConstruction)(state: ServerData): ServerData = {
    val base = state.bases(command.objectId)
    state.copy(
      bases = state.bases.updated(command.objectId, base.copy(
        shipBeingBuilt = None
      ))
    )
  }

  def getStarbaseState(baseId: PlanetId)(specs: Specs): Starbase = {
    stateAfterCommands(specs).bases(baseId)
  }

  def getStarbaseInitial(baseId: PlanetId): Starbase = {
    initialState.bases(baseId)
  }

  def getPlanetState(planetId: PlanetId)(specs: Specs): PlanetRecord = {
    stateAfterCommands(specs).planets(planetId)
  }

  def getPlanetInitial(planetId: PlanetId): PlanetRecord = {
    initialState.planets(planetId)
  }
}
