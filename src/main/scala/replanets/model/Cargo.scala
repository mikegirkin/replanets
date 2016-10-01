package replanets.model

import replanets.common.Minerals

case class Cargo(
  neu: Int = 0,
  tri: Int = 0,
  dur: Int = 0,
  mol: Int = 0,
  supplies: Int = 0,
  colonists: Int = 0,
  money: Int = 0,
  torps: Int = 0,
  fighters: Int = 0
) {
  def weight = tri + dur + mol + supplies + colonists + torps + fighters

  def minus(other: Cargo): Cargo = {
    Cargo(
      neu = neu - other.neu,
      tri = tri - other.tri,
      dur = dur - other.dur,
      mol = mol - other.mol,
      supplies = supplies - other.supplies,
      colonists = colonists - other.colonists,
      money = money - other.money,
      torps = torps - other.torps,
      fighters = fighters - other.fighters
    )
  }

  def plus(other: Cargo): Cargo = {
    this.minus(Cargo.zero.minus(other))
  }

  def minerals = Minerals(
    neu,
    tri,
    dur,
    mol
  )
}

object Cargo {
  def zero = Cargo()
}
