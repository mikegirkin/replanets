package replanets.common

case class Cost(
  tri: Int = 0,
  dur: Int = 0,
  mol: Int = 0,
  money: Int = 0
) {
  def mul(n: Int) = Cost(this.tri * n, this.dur * n, this.mol * n, this.money * n)

  def add(other: Cost)  = Cost(this.tri + other.tri, this.dur + other.dur, this.mol + other.mol, this.money + other.money)

  def sub(other: Cost) = this.add(other.mul(-1))
}

object Cost {
  def zero = Cost(0, 0, 0, 0)
}