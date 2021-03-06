package replanets.model

import replanets.common._
import NumberExtensions._

case class MoneySupplies(
  money: Int,
  supplies: Int
)

trait Formulas {
  val minefieldDecayRatio = 5

  def unitsInMinefieldByRadius(radius: Int): Int = {
    radius * radius
  }

  def minefieldUnitsNextTurn(units: Int): Int = {
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

  def erndDiv(dividend: Int, divisor: Int): Int = {
    val whole = dividend / divisor
    val reminder = dividend % divisor
    if(reminder*2 == divisor) {
      if (whole % 2 == 1) whole + 1 else whole
    } else {
      if(reminder*2 > divisor) whole + 1 else whole
    }
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

  def miningRate(density: Int, mines: Int, raceId: RaceId, nativeId: NativeRace) = {
    val miningRateConfig = 100
    val raceFactor = if(raceId == RaceId(2)) 2 else 1 //Lizards
    val nativeFactor = if(nativeId == NativeRace.Reptilian) 2 else 1 //Reptilians
    erndDiv(erndDiv(mines * density, 100) * miningRateConfig, 100) * raceFactor * nativeFactor
  }

  def techUpgradeCost(available: Int, desired: Int) = {
    if(available >= desired) 0
    else if(available >= 10) 0
    else Constants.techLevelsCost.slice(available, desired).sum
  }

  def maxAchievableTechLevel(available: Int, fundsAvailable: Int) = {
    (available to 10)
      .map(lvl => (lvl, techUpgradeCost(available, lvl)))
      .filter {case (lvl, cost) => cost <= fundsAvailable }
      .last
      ._1
  }

  def remainingMoneySupplies(available: MoneySupplies, cost: Int): MoneySupplies = {
    MoneySupplies.tupled(
      if(available.money < cost) (0, available.supplies - (cost - available.money))
      else (available.money - cost, available.supplies)
    )
  }

  //This formula has been taken from phost docs
  def nativeTaxIncome(
    playerRace: RaceId,
    nativeRace: NativeRace,
    nativeGovernment: NativeGovernment,
    nativeClans: Int,
    nativeTaxRate: Int,
    nativeHappiness: Int,
    colonistClans: Int
  ): Int = {
    val cleanTax = {
      val nativesDue = Math.round((nativeClans.toDouble / 10) * (nativeGovernment.taxPercentage.toDouble / 100) * (nativeTaxRate.toDouble / 100))
      Math.min(colonistClans, nativesDue)
    }

    val nativeFactor = if(nativeRace == NativeRace.Amorphous) 0 //Amorphous
    else if(nativeRace == NativeRace.Insectoid) 2 //Insectoid
    else 1

    //TODO: This should be taken from parsed configs
    val playerRaceFactor = if(playerRace == RaceId(1)) 200.toDouble else 100.toDouble

    Math.round(cleanTax * nativeFactor * (playerRaceFactor / 100)).toInt
  }

  def colonistTaxIncome(
    playerRace: RaceId,
    clans: Int,
    taxRate: Int,
    happiness: Int
  ): Int = {

    val playerRaceFactor = if(playerRace == RaceId(1)) 200.toDouble else 100.toDouble

    Math.round(Math.round((clans.toDouble / 10) * (taxRate.toDouble / 100)) * (playerRaceFactor / 100)).toInt
  }

  def nativeHappinessChange(
    nativeRace: NativeRace,
    government: NativeGovernment,
    nativeClans: Int,
    mines: Int,
    factories: Int,
    nativeTax: Int
  ): Int = {
    (5 + (government.id.value.toDouble / 2) -
      Math.sqrt(nativeClans.toDouble / 10000) -
      (mines + factories).toDouble / 200 -
      nativeTax * 0.85 +
      (if(nativeRace == NativeRace.Avian) 10 else 0)).toInt
  }

  def colonistHappinessChange(
    race: RaceId,
    colonistClans: Int,
    planetTemperature: Int,
    mines: Int,
    factories: Int,
    colonistTax: Int
  ): Int = {
    val targetTemp = if(race == RaceId(7)) 100 else 50
    val tempDivisor = if(race == RaceId(7)) 66 else 33

    (10 - Math.sqrt(colonistClans.toDouble / 10000)
      - Math.abs(planetTemperature - targetTemp).toDouble / tempDivisor
      - (mines + factories).toDouble / 300
      - colonistTax * 0.8).toInt
  }

  private def maxStructuresForMoney(
    supplies: Int,
    money: Int,
    cost: Int
  ) = {
    val moneyCostPer1 = cost - 1
    if(supplies * moneyCostPer1 <= money) supplies
    else {
      val forFullMoney: Int = money / moneyCostPer1
      val moneyLeft = money % moneyCostPer1
      val suppliesUsedForPartial = cost - moneyLeft
      val suppliesLeft = supplies - forFullMoney - suppliesUsedForPartial
      forFullMoney +
        (if(suppliesLeft >= 0) 1 else 0) +
        (if(suppliesLeft >= cost) suppliesLeft/cost else 0)
    }
  }

  def maxFactoriesForMoney(supplies: Int, money: Int): Int = {
    maxStructuresForMoney(supplies, money, 4)
  }

  def maxMinesForMoney(supplies: Int, money: Int): Int = {
    maxStructuresForMoney(supplies, money, 5)
  }

  def maxDefencesForMoney(supplies: Int, money: Int): Int = {
    maxStructuresForMoney(supplies, money, 11)
  }

  def remainingResourcesAfterStructuresBuilt(
    structureCost: Int
  )(
    structuresToBuild: Int, planetSupplies: Int, planetMoney: Int
  ): (Int, Int) = {
    val moneyCost = structureCost - 1
    val moneyRemaining = (planetMoney - structuresToBuild * moneyCost).lowerBound(0)
    val costUnpaid = structuresToBuild + (structuresToBuild * moneyCost - planetMoney).lowerBound(0)
    val suppliesRemaining = planetSupplies - costUnpaid

    (suppliesRemaining, moneyRemaining)
  }
}
