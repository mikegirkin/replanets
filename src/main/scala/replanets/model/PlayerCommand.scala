package replanets.model

import replanets.common.Fcode

/**
  * Created by mgirkin on 09/08/2016.
  */
trait PlayerCommand {
  def isReplacableBy(other: PlayerCommand): Boolean
  def changesSomething(game: Game, turn: Int, race: Int): Boolean
}

case class SetPlanetFcode(
  planetId: Int,
  newFcode: Fcode
) extends PlayerCommand {
  override def isReplacableBy(other: PlayerCommand): Boolean = {
    other match {
      case SetPlanetFcode(otherPlanetId, _) if planetId == otherPlanetId => true
      case _ => false
    }
  }

  override def changesSomething(game: Game, turn: Int, race: Int): Boolean = {
    val changed = for(
      turn <- game.turns.get(turn);
      rst <- turn.rstFiles.get(race);
      planet <- rst.planets.find(_.planetId == planetId)
    ) yield planet.fcode != newFcode
    changed.getOrElse(false)
  }
}

case class SetShipFcode(
  shipId: Int,
  newFcode: Fcode
) extends PlayerCommand {
  override def isReplacableBy(other: PlayerCommand): Boolean = {
    other match {
      case SetShipFcode(otherShipId, _) if shipId == otherShipId => true
      case _ => false
    }
  }

  override def changesSomething(game: Game, turn: Int, race: Int): Boolean = true

}
