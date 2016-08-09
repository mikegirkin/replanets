package replanets.model

import replanets.common.Fcode

/**
  * Created by mgirkin on 09/08/2016.
  */
trait PlayerCommand {
  def isReplacableBy(other: PlayerCommand): Boolean
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
}
