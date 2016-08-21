package replanets.common

/**
  * Created by mgirkin on 10/08/2016.
  */
class ImpossibleOneBasedIndex(value: Int) extends Exception

sealed trait OneBasedIndex {
  val value: Int
  if (!OneBasedIndex.isValidValue(value)) throw new ImpossibleOneBasedIndex(value)
}

object OneBasedIndex {
  def isValidValue(value: Int) = value > 0
}

case class Id(value: Int) extends OneBasedIndex
case class HullId(value: Int) extends OneBasedIndex
case class EngineId(value: Int) extends OneBasedIndex
case class BeamId(value: Int) extends OneBasedIndex
case class LauncherId(value: Int) extends OneBasedIndex
case class RaceId(value: Int) extends OneBasedIndex
case class TurnId(value: Int) extends OneBasedIndex
case class PlanetId(value: Int) extends OneBasedIndex
case class ShipId(value: Int) extends OneBasedIndex