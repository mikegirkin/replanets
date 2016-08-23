package replanets.common

case class NativeRace(
  id: Id,
  name: String
)

object NativeRace {
  object None extends NativeRace(Id(0), "none")
  object Humanoid extends NativeRace(Id(1), "Humanoid")
  object Bovinoid extends NativeRace(Id(2), "Bovinoid")
  object Reptilian extends NativeRace(Id(3), "Reptilian")
  object Avian extends NativeRace(Id(4), "Avian")
  object Amorphous extends NativeRace(Id(5), "Amorphous")
  object Insectoid extends NativeRace(Id(6), "Insectoid")
  object Amphibian extends NativeRace(Id(7), "Amphibian")
  object Ghipsoldal extends NativeRace(Id(8), "Ghipsoldal")
  object Siliconoid extends NativeRace(Id(9), "Siliconoid")
}