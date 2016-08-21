package replanets.model

import replanets.common._

trait PlayerCommand {
  def objectId: OneBasedIndex
  def isReplacableBy(other: PlayerCommand): Boolean
  def changesSomething(game: Game, turn: TurnId, race: RaceId): Boolean
}

trait PlanetPlayerCommand extends PlayerCommand {
  override def objectId: PlanetId
}

trait ShipPlayerCommand extends PlayerCommand {
  override def objectId: ShipId
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

  override def changesSomething(game: Game, turn: TurnId, race: RaceId): Boolean = {
    val changed = for(
      turn <- game.turns.get(turn);
      raceTurn <- turn.get(race);
      rst = raceTurn.rst;
      planet <- rst.planets.find(_.planetId == objectId.value)
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

  override def changesSomething(game: Game, turn: TurnId, race: RaceId): Boolean = true

}

case class BuildShip(
  objectId: PlanetId, hullId: HullId, engineId: EngineId,
  beamId: BeamId, beamsCount: Int,
  launcherId: LauncherId, launcherCount: Int
) extends PlayerCommand {

  def this(
    objectId: PlanetId,
    buildOrder: ShipBuildOrder) =
    this(
      objectId, buildOrder.hull.id, buildOrder.engine.id,
      buildOrder.beam.id, buildOrder.beamCount,
      buildOrder.launchers.id, buildOrder.launcherCount
    )

  def getBuildOrder(specs: Specs) = ShipBuildOrder(
    specs.hullSpecs.find(_.id == hullId).get,
    specs.engineSpecs.find(_.id == engineId).get,
    specs.beamSpecs.find(_.id == beamId).get,
    beamsCount,
    specs.torpSpecs.find(_.id == launcherId).get,
    launcherCount
  )

  override def isReplacableBy(other: PlayerCommand): Boolean = {
    other match {
      case BuildShip(baseId, _, _, _, _, _, _) if baseId == objectId => true
      case _ => false
    }
  }

  override def changesSomething(game: Game, turn: TurnId, race: RaceId): Boolean = {
    val base = game.turnInfo(turn).getStarbaseState(objectId)
    if(base.shipBeingBuilt.contains(getBuildOrder(game.specs))) false else true
  }
}
