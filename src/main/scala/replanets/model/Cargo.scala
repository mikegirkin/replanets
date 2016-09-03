package replanets.model

case class Cargo(
  neu: Int = 0,
  tri: Int = 0,
  dur: Int = 0,
  mol: Int = 0,
  supplies: Int = 0,
  colonists: Int = 0,
  money: Int = 0,
  torps: Int = 0
) {
  def weight = tri + dur + mol + supplies + colonists + torps

  def minus(other: Cargo): Cargo = {
    Cargo(
      neu = neu - other.neu,
      tri = tri - other.tri,
      dur = dur - other.dur,
      mol = mol - other.mol,
      supplies = supplies - other.supplies,
      colonists = colonists - other.colonists,
      money = money - other.money,
      torps = torps - other.torps
    )
  }
}
