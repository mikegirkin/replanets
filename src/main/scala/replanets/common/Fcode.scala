package replanets.common

import scala.util.Random
/**
  * Created by mgirkin on 09/08/2016.
  */
class ImpossibleFcode extends Exception

case class Fcode(
  value: String
) {
  if(!Fcode.acceptableFcode(value)) throw new ImpossibleFcode
}

object Fcode {
  import NumberExtensions._

  def acceptableFcode(string: String) = string.length == 3 && string.forall(c => c.toInt.between(32, 122))

  def tryConvert(string: String): Option[Fcode] = {
    if(acceptableFcode(string)) Some(new Fcode(string))
    else None
  }

  def random(): Fcode = {
    val randomChars = Random.alphanumeric
    Fcode(new String(randomChars.take(3).toArray))
  }
}