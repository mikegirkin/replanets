package replanets.common

/**
  * Created by mgirkin on 10/08/2016.
  */
class ImpossibleOneBasedIndex(value: Int) extends Exception

sealed trait Index {
  val value: Int
}

sealed trait OneBasedIndex extends Index{
  val value: Int
  if (!OneBasedIndex.isValidValue(value)) throw new ImpossibleOneBasedIndex(value)
}

object OneBasedIndex {
  def isValidValue(value: Int) = value > 0
}

case class Id(value: Int) extends OneBasedIndex
case class HullId(value: Int) extends OneBasedIndex
case class EngineId(value: Int) extends OneBasedIndex

case class BeamId(value: Int) extends Index
object BeamId {
  val Nothing = BeamId(0)
}

case class LauncherId(value: Int) extends Index
object LauncherId {
  val Nothing = LauncherId(0)
}

case class RaceId(value: Int)
case class TurnId(value: Int) extends OneBasedIndex
case class PlanetId(value: Int) extends OneBasedIndex
case class ShipId(value: Int) extends OneBasedIndex