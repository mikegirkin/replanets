package replanets.model

import replanets.common.{Fcode, RaceId, TurnId}

/**
  * Created by mgirkin on 09/08/2016.
  */
trait ObjectId {
  val value: Int
}
case class PlanetId(value: Int) extends ObjectId
case class ShipId(value: Int) extends ObjectId

trait PlayerCommand {
  def objectId: ObjectId
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
