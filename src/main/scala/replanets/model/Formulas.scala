package replanets.model

import replanets.common._
import NumberExtensions._

trait Formulas {
  def minefieldUnitsNextTurn(units: Int): Int
  def unitsInMinefieldByRadius(radius: Int): Int
  def mineHitDamage(hull: HullspecItem): Int
  def webHitDamage(hull: HullspecItem): Int
  def fuelBurn(engine: EngspecItem, warp: Int, mass: Int, dx: Int, dy: Int, isGravitonic: Boolean): Int

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
