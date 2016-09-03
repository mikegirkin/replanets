package replanets.model

case class CargoHold(
  neuCapacityTotal: Int,
  cargoCapacityTotal: Int,
  maxIndividual: Int,
  protected var _cargo: Cargo
) {
  def cargo = _cargo
  def neuCapacityLeft = neuCapacityTotal - cargo.neu
  def cargoCapacityLeft = cargoCapacityTotal - cargo.weight
  def moneyCapacityLeft = maxIndividual - cargo.money

  def add(transfer: Cargo): Unit = {
    if (transfer.weight > cargoCapacityLeft) throw new UnsupportedOperationException
    if (transfer.neu > neuCapacityLeft) throw new UnsupportedOperationException
    if (transfer.money > moneyCapacityLeft) throw new UnsupportedOperationException
    val newCargo = Cargo(
      cargo.neu + transfer.neu,
      cargo.tri + transfer.tri,
      cargo.dur + transfer.dur,
      cargo.mol + transfer.mol,
      cargo.supplies + transfer.supplies,
      cargo.colonists + transfer.colonists,
      cargo.money + transfer.money
    )
    if(newCargo.neu < 0 || newCargo.mol < 0 || newCargo.tri < 0 || newCargo.dur < 0
      || newCargo.supplies < 0 || newCargo.colonists < 0 || newCargo.money < 0 || newCargo.torps < 0)
      throw new UnsupportedOperationException
    _cargo = newCargo
  }
}

object CargoHold {
  def zero = CargoHold(0, 0, 0, Cargo())
}