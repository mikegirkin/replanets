package replanets.model

import replanets.common._

sealed trait PlayerCommand {
  def objectId: OneBasedIndex
  def isReplacableBy(other: PlayerCommand): Boolean
  def isAddDiffToInitialState(game: Game, turn: TurnId, race: RaceId): Boolean
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

  override def isAddDiffToInitialState(game: Game, turn: TurnId, race: RaceId): Boolean = {
    val changed = for(
      turn <- game.turns.get(turn);
      raceTurn <- turn.get(race);
      rst = raceTurn.initialState;
      planet <- rst.planets.get(objectId)
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

  override def isAddDiffToInitialState(game: Game, turn: TurnId, race: RaceId): Boolean = true

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

  def getBuildOrder(specs: Specs) = ShipBuildOrder(
    specs.hullSpecs.find(_.id == hullId).get,
    specs.engineSpecs.find(_.id == engineId).get,
    if(beamId != BeamId.Nothing) Some(BeamsOrder(specs.beamSpecs.find(_.id == beamId).get, beamsCount)) else None,
    if(launcherId != LauncherId.Nothing) Some(LaunchersOrder(specs.torpSpecs.find(_.id == launcherId).get, launcherCount)) else None
  )

  override def isReplacableBy(other: PlayerCommand): Boolean = {
    other match {
      case StartShipConstruction(baseId, _, _, _, _, _, _) if baseId == objectId => true
      case StopShipConstruction(baseId) if baseId == objectId => true
      case _ => false
    }
  }

  override def isAddDiffToInitialState(game: Game, turn: TurnId, race: RaceId): Boolean = {
    val base = game.turnInfo(turn).getStarbaseState(objectId)(game.specs)
    if(base.shipBeingBuilt.contains(getBuildOrder(game.specs))) false else true
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

  override def isAddDiffToInitialState(game: Game, turn: TurnId, race: RaceId): Boolean = {
    val base = game.turnInfo(turn).getStarbaseInitial(objectId)
    if(base.shipBeingBuilt.isDefined) true else false
  }

}