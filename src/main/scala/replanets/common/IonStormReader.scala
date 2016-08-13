package replanets.common

import replanets.recipes.{BinaryReadRecipe, WORD}
import NumberExtensions._

/**
  * Created by mgirkin on 04/08/2016.
  */
case class IonStorm(
  id: Int,
  x: Short,
  y: Short,
  radius: Short,
  voltage: Short,
  warp: Short,
  heading: Short
) {
  def category = {
    if(voltage.between(1, 49)) 1
    else if (voltage.between(50, 99)) 2
    else if (voltage.between(100, 149)) 3
    else if (voltage.between(150, 199)) 4
    else if (voltage >= 200) 5
    else 0
  }

  def isGrowing: Boolean = {
    voltage % 2 == 1
  }
}

object IonStormReader {
  import IteratorExtensions._

  val maxNumberOfStorms = 50

  def read(it: Iterator[Byte]): IndexedSeq[IonStorm] = {
    (1 to 50).map { idx =>
      it.read(new BinaryReadRecipe[IonStorm] {
        override def read(source: Iterator[Byte]): IonStorm = {
          IonStorm(
            idx,
            it.read(WORD),
            it.read(WORD),
            it.read(WORD),
            it.read(WORD),
            it.read(WORD),
            it.read(WORD)
          )
        }
      })
    }.filter(_.voltage > 0)
  }
}
