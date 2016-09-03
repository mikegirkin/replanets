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
}
