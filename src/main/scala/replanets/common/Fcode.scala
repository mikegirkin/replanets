package replanets.common

import scala.util.Random
/**
  * Created by mgirkin on 09/08/2016.
  */
class ImpossibleFcode extends Exception

case class Fcode(value: String) {
  if(!Fcode.isValid(value)) throw new ImpossibleFcode
}

object Fcode {
  import replanets.recipes.NumberExtensions._

  def isValid(string: String): Boolean = {
    string.length == 3 && string.forall(c => c.toInt.between(32, 122))
  }

  def tryConvert(string: String): Option[Fcode] = {
    if(isValid(string)) Some(Fcode(string))
    else None
  }

  def random(): Fcode = {
    val randomChars = Random.alphanumeric
    Fcode(new String(randomChars.take(3).toArray))
  }
}
