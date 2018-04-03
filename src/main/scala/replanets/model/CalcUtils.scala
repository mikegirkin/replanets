package replanets.model

import replanets.common.{Cost, Minerals}

object CalcUtils {

  /***
    * Returns a / b but in case b = 0 returns saturator
    */
  private def saturatedDiv(a: Int, b: Int, saturator: Int = Int.MaxValue): Int =
    if(b == 0) saturator
    else a / b

  def maxPossibleUnitsForGivenCost(minerals: Minerals, money: Int, supplies: Int, cost: Cost) = {
    Seq(
      saturatedDiv(minerals.tritanium, cost.tri),
      saturatedDiv(minerals.duranium, cost.dur),
      saturatedDiv(minerals.molybdenium, cost.mol),
      saturatedDiv(money + supplies, cost.money)
    ).min
  }
}
