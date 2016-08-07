package replanets.model

/**
  * Created by mgirkin on 06/08/2016.
  */
object THostFormulas extends Formulas{
  def maxFactories(colonistClans: Int):Int = {
    if(colonistClans <= 100) colonistClans
    else Math.round(100 + Math.sqrt(colonistClans - 100)).asInstanceOf[Int]
  }

  def maxMines(colonistClans: Int): Int = {
    if(colonistClans <= 200) colonistClans
    else Math.round(200 + Math.sqrt(colonistClans - 200)).asInstanceOf[Int]
  }

  def maxDefences(colonistClans: Int): Int = {
    if(colonistClans <= 50) colonistClans
    else Math.round(50 + Math.sqrt(colonistClans - 50)).asInstanceOf[Int]
  }

  def erndDiv(dividend: Int, divisor: Int): Int = {
    val whole = dividend / divisor
    val reminder = dividend % divisor
    if(reminder*2 == divisor) {
      if (whole % 2 == 1) whole + 1 else whole
    } else {
      if(reminder*2 > divisor) whole + 1 else whole
    }
  }

  val miningRateConfig = 100

  def miningRate(density: Int, mines: Int, raceId: Int, nativeId: Int) = {
    val raceFactor = if(raceId == 2) 2 else 1 //Lizards
    val nativeFactor = if(nativeId ==3) 2 else 1 //Reptilians
    erndDiv(erndDiv(mines * density, 100) * miningRateConfig, 100) * raceFactor * nativeFactor
  }
}