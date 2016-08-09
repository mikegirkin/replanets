package replanets.common

import scala.util.Random
/**
  * Created by mgirkin on 09/08/2016.
  */
class ImpossibleFcode extends Exception

case class Fcode(chars: Iterable[Char]) {
  def asString = new String(chars.toArray)
}

object Fcode {
  import replanets.recipes.NumberExtensions._

  def apply(string: String) = {
    tryConvert(string).getOrElse {
      throw new ImpossibleFcode
    }
  }

  def tryConvert(string: String): Option[Fcode] = {
    if(string.length == 3 && string.forall(c => c.toInt.between(32, 122))) Some(new Fcode(string.toCharArray))
    else None
  }

  def random(): Fcode = {
    val randomChars = Random.alphanumeric
    Fcode(randomChars.take(3).toArray)
  }
}
