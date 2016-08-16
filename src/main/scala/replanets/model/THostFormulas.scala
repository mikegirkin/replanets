package replanets.model

import replanets.common.{EngspecItem, HullspecItem}

/**
  * Created by mgirkin on 06/08/2016.
  */
object THostFormulas extends Formulas{
  val minefieldDecayRatio = 5

  def unitsInMinefieldByRadius(radius: Int): Int = {
    radius * radius
  }

  override def minefieldUnitsNextTurn(units: Int): Int = {
    (Math.round(units * (1.0 - minefieldDecayRatio/100.0)) - 1).toInt
  }

  def mineHitDamage(hull: HullspecItem): Int = {
    val damage = Math.round(10000/(hull.mass + 1.0)).toInt
    if(damage > 100) 100 else damage
  }

  def webHitDamage(hull: HullspecItem): Int = {
    val damage = Math.round(10000/(hull.mass + 1.0)).toInt / 10
    if(damage > 100) 100 else damage
  }

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

  def fuelBurn(engine: EngspecItem, warp: Int, mass: Int, dx: Int, dy: Int, isGravitonic: Boolean): Int = {
    //TODO: This formula has to be validated
    //TODO: This formula is not completely adhere to THost calculation, ref http://www.phost.de/~stefan/fuelusage.html
    val fuelUsage =
      if (warp == 0) 0
      else engine.fuel(warp - 1)
    val distance = Math.sqrt(dx * dx + dy * dy)
    val gf = if(isGravitonic) 2 else 1
    val way = warp * warp * gf
    val usageDouble = (fuelUsage * (mass / 10) * distance.toInt).toDouble / (10000 * way)
    usageDouble.toInt
  }
}