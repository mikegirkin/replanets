package replanets.common

case class NativeGovernment(
  id: Id,
  name: String,
  taxPercentage: Int
)

object NativeGovernment {
  object None extends NativeGovernment(Id(0), "none", 0)
  object Anarchy extends NativeGovernment(Id(1), "Anarchy", 20)
  object PreTribal extends NativeGovernment(Id(2), "Pre-Tribal", 40)
  object EarlyTribal extends NativeGovernment(Id(3), "Early-Tribal", 60)
  object Tribal extends NativeGovernment(Id(4), "Tribal", 80)
  object Feudal extends NativeGovernment(Id(5), "Feudal", 100)
  object Monarchy extends NativeGovernment(Id(6), "Monarchy", 120)
  object Representative extends NativeGovernment(Id(7), "Representative", 140)
  object Participatory extends NativeGovernment(Id(8), "Participatory", 160)
  object Unity extends NativeGovernment(Id(9), "Unity", 180)
}