package replanets.common

import scala.util.Random
/**
  * Created by mgirkin on 09/08/2016.
  */
class ImpossibleFcode extends Exception

trait Fcode {
  def value: String

  override def toString: String = s"Fcode($value)"
}

object Fcode {
  import replanets.recipes.NumberExtensions._

  def apply(value: String): Fcode =
    Fcode.tryConvert(value).getOrElse {
      throw new ImpossibleFcode
    }

  def tryConvert(string: String): Option[Fcode] = {
    if(string.length == 3 && string.forall(c => c.toInt.between(32, 122))) Some(new Fcode { val value = string })
    else None
  }

  def random(): Fcode = {
    val randomChars = Random.alphanumeric
    Fcode(new String(randomChars.take(3).toArray))
  }
}