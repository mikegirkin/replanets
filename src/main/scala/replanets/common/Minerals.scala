package replanets.common

case class Minerals(
  neutronium: Int,
  tritanium: Int,
  duranium: Int,
  molybdenium: Int
) {
  def minus(transfer: Minerals) = {
    Minerals(
      neutronium = this.neutronium - transfer.neutronium,
      tritanium = this.tritanium - transfer.tritanium,
      duranium = this.duranium - transfer.duranium,
      molybdenium = this.molybdenium - transfer.molybdenium
    )
  }

  def plus(transfer: Minerals) = {
    this.minus(Minerals.zero.minus(transfer))
  }

  def minus(cost: Cost) = {
    Minerals(
      neutronium,
      tritanium = this.tritanium - cost.tri,
      duranium = this.duranium - cost.dur,
      molybdenium = this.molybdenium - -cost.mol
    )
  }
}

object Minerals {
  def zero = Minerals(0, 0, 0, 0)
}
