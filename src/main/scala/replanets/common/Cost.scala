package replanets.common

case class Cost(
  tri: Int,
  dur: Int,
  mol: Int,
  money: Int
) {
  def mul(n: Int) = Cost(this.tri * n, this.dur * n, this.mol * n, this.money * n)

  def add(other: Cost)  = Cost(this.tri + other.tri, this.dur + other.dur, this.mol + other.mol, this.money + other.money)

  def sub(other: Cost) = this.add(other.mul(-1))
}

object Cost {
  def zero = Cost(0, 0, 0, 0)
}