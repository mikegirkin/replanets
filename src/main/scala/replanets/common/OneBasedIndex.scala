package replanets.common

/**
  * Created by mgirkin on 10/08/2016.
  */
class ImpossibleOneBasedIndex(value: Int) extends Exception

case class OneBasedIndex(value: Int) {
  if(!OneBasedIndex.isValidValue(value)) throw new ImpossibleOneBasedIndex(value)
}

object OneBasedIndex {
  def isValidValue(value: Int) = value > 0

  def tryConvert(value: Int) = if(isValidValue(value)) Some(OneBasedIndex(value))
  else None
}
