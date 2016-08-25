package replanets.model.commands

import replanets.common._
import replanets.model.ShipBuildOrder

trait PlayerCommand {
  def objectId: OneBasedIndex
  def isReplacableBy(other: PlayerCommand): Boolean
  def isAddDiffToInitialState(initial: ServerData): Boolean
}

case class SetPlanetFcode(
  objectId: PlanetId,
  newFcode: Fcode
) extends PlayerCommand {
  override def isReplacableBy(other: PlayerCommand): Boolean = {
    other match {
      case SetPlanetFcode(otherPlanetId, _) if objectId == otherPlanetId => true
      case _ => false
    }
  }

  override def isAddDiffToInitialState(initial: ServerData): Boolean = {
    val changed = for(
      planet <- initial.planets.get(objectId)
    ) yield planet.fcode.value != newFcode.value
    changed.getOrElse(false)
  }
}

case class SetShipFcode(
  objectId: ShipId,
  newFcode: Fcode
) extends PlayerCommand {
  override def isReplacableBy(other: PlayerCommand): Boolean = {
    other match {
      case SetShipFcode(otherShipId, _) if objectId == otherShipId => true
      case _ => false
    }
  }

  override def isAddDiffToInitialState(initial: ServerData): Boolean = {
    if(initial.ships(objectId).asInstanceOf[OwnShip].fcode != newFcode) true else false
  }

}

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
}

case class StopShipConstruction(
  objectId: PlanetId
) extends PlayerCommand {

  override def isReplacableBy(other: PlayerCommand): Boolean = {
    other match {
      case StartShipConstruction(baseId, _, _, _, _, _, _) if baseId == objectId => true
      case _ => false
    }
  }

  override def isAddDiffToInitialState(initial: ServerData): Boolean = {
    val base = initial.bases(objectId)
    if(base.shipBeingBuilt.isDefined) true else false
  }

}