package replanets.common

import replanets.recipes.{BinaryReadRecipe, WORD}
import replanets.recipes.NumberExtensions._

/**
  * Created by mgirkin on 04/08/2016.
  */
case class IonStorm(
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
}

object IonStormReader {
  import replanets.recipes.IteratorExtensions._

  def read(it: Iterator[Byte]): IndexedSeq[IonStorm] = {
    it.read(new BinaryReadRecipe[IonStorm] {
      override def read(source: Iterator[Byte]): IonStorm = {
        IonStorm(
          it.read(WORD),
          it.read(WORD),
          it.read(WORD),
          it.read(WORD),
          it.read(WORD),
          it.read(WORD)
        )
      }
    }, 50).filter(_.voltage > 0)
  }
}
