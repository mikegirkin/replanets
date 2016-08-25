package replanets.model.commands

import replanets.common.{ServerData, _}
import replanets.model.{ShipBuildOrder, Specs}

case class StartShipConstruction(
  objectId: PlanetId, hullId: HullId, engineId: EngineId,
  beamId: BeamId, beamsCount: Int,
  launcherId: LauncherId, launcherCount: Int
) extends PlayerCommand {

  def this(
    objectId: PlanetId,
    buildOrder: ShipBuildOrder) =
    this(
      objectId, buildOrder.hull.id, buildOrder.engine.id,
      buildOrder.beams.map(_.spec.id).getOrElse(BeamId.Nothing),
      buildOrder.beams.map(_.count).getOrElse(0),
      buildOrder.launchers.map(_.spec.id).getOrElse(LauncherId.Nothing),
      buildOrder.launchers.map(_.count).getOrElse(0)
    )


  override def isReplacableBy(other: PlayerCommand): Boolean = {
    other match {
      case StartShipConstruction(baseId, _, _, _, _, _, _) if baseId == objectId => true
      case StopShipConstruction(baseId) if baseId == objectId => true
      case _ => false
    }
  }

  private def areEqualOrders(order: ShipBuildOrder,
    hullId: HullId, engineId: EngineId,
    beamId: BeamId, beamCount: Int, launcherId: LauncherId, launcherCount: Int
  ): Boolean = {
    val hullsEqual = order.hull.id == hullId
    val enginesEqual = order.engine.id == engineId
    val beamsEqual = {
      (order.beams.isEmpty && beamId == BeamId.Nothing) ||
      order.beams.exists(bo => bo.spec.id == beamId && bo.count == beamCount)
    }
    val launchersEqual = {
      (order.launchers.isEmpty && launcherId == LauncherId.Nothing) ||
      order.launchers.exists(lo => lo.spec.id == launcherId && lo.count == launcherCount)
    }
    hullsEqual && enginesEqual && beamsEqual && launchersEqual
  }

  override def isAddDiffToInitialState(initial: ServerData): Boolean = {
    val base = initial.bases(objectId)
    !base.shipBeingBuilt.exists(bo => areEqualOrders(bo, hullId, engineId, beamId, beamsCount, launcherId, launcherCount))
  }

  override def apply(state: ServerData, specs: Specs): ServerData = {
    val order = ShipBuildOrder.getBuildOrder(specs)(
      hullId, engineId, beamId, beamsCount, launcherId, launcherCount)
    val base = state.bases(objectId)
    val hullTechLevel = Math.max(order.hull.techLevel, base.hullsTech)
    val engineTechLevel = Math.max(order.engine.techLevel, base.engineTech)
    val beamTechLevel = Math.max(order.beams.map(_.spec.techLevel).getOrElse(1), base.beamTech)
    val torpsTechLevel = Math.max(order.launchers.map(_.spec.techLevel).getOrElse(1), base.torpedoTech)
    val cost = base.shipCostAtStarbase(order)
    val planet = state.planets(objectId)
    val newPlanetMoney = if(planet.money >= cost.total.money) planet.money - cost.total.money else 0
    val newPlanetSupplies = {
      if(planet.money >= cost.total.money) planet.supplies
      else planet.supplies - (cost.total.money - planet.money)
    }
    state.copy(
      bases = state.bases.updated(objectId, base.copy(
        shipBeingBuilt = Some(order),
        hullsTech = hullTechLevel,
        engineTech = engineTechLevel,
        beamTech = beamTechLevel,
        torpedoTech = torpsTechLevel
      )),
      planets = state.planets.updated(objectId, planet.copy(
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
}
