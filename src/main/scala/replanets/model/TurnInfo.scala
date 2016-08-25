package replanets.model

import replanets.common._
import replanets.model.commands._

import scala.collection.mutable

case class TurnInfo(
  specs: Specs,
  initialState: ServerData
) {

  private val _commands: mutable.Buffer[PlayerCommand] = mutable.Buffer()

  private var _stateAfterCommands: ServerData = initialState

  def stateAfterCommands: ServerData = _stateAfterCommands

  private def applyCommand(state: ServerData, command: PlayerCommand): ServerData = {
    command match {
      case x:SetPlanetFcode => handle(x)(state)
      case x:SetShipFcode => handle(x)(state)
      case x:StartShipConstruction => handle(x)(state)
      case x:StopShipConstruction => handle(x)(state)
      case x:SetColonistTax => x.apply(state)
      case x:SetNativeTax => x.apply(state)
      case _ => state
    }
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

  private def handle(command: StartShipConstruction)(state: ServerData): ServerData = {
    val order = ShipBuildOrder.getBuildOrder(specs)(
      command.hullId, command.engineId, command.beamId, command.beamsCount, command.launcherId, command.launcherCount)
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
        shipBeingBuilt = Some(order),
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

  def commands: Seq[PlayerCommand] = Seq(_commands:_*)

  def addCommand(cmds: PlayerCommand*): Unit = {
    cmds.foreach( cmd => {
      val oldCommandIndex = _commands.indexWhere(p => p.isReplacableBy(cmd))
      val changesSomething = cmd.isAddDiffToInitialState(initialState)
      if (oldCommandIndex >= 0 && changesSomething)
        _commands(oldCommandIndex) = cmd
      else if (oldCommandIndex >= 0 && !changesSomething)
        _commands.remove(oldCommandIndex)
      else if (changesSomething)
        _commands.append(cmd)
    })
    _stateAfterCommands = commands.foldLeft(initialState)(applyCommand)
  }

  def withCommands(cmds: PlayerCommand*): TurnInfo = {
    addCommand(cmds:_*)
    this
  }

  def getStarbaseState(baseId: PlanetId): Starbase = {
    stateAfterCommands.bases(baseId)
  }

  def getStarbaseInitial(baseId: PlanetId): Starbase = {
    initialState.bases(baseId)
  }

  def getPlanetState(planetId: PlanetId): PlanetRecord = {
    stateAfterCommands.planets(planetId)
  }

  def getPlanetInitial(planetId: PlanetId): PlanetRecord = {
    initialState.planets(planetId)
  }
}
