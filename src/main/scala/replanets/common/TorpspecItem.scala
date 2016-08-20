package replanets.common

import java.nio.file.{Files, Path}

import replanets.recipes._

case class TorpspecItem(
  id: LauncherId,
  name: String,
  torpedoMoneyCost: Short,
  launcherCost: Cost,
  mass: Short,
  techLevel: Short,
  kill: Short,
  damage: Short
)

object TorpspecItem {
  val nameLength = 20

  def fromFile(file: Path): IndexedSeq[TorpspecItem] = {
    val it = Files.readAllBytes(file).iterator
    (1 to Constants.TorpspecRecordsNumber).map { idx =>
      TorpspecItem(
        LauncherId(idx),
        SpacePaddedString(nameLength).read(it),
        WORD.read(it),
        Cost(
          money = WORD.read(it),
          tri = WORD.read(it),
          dur = WORD.read(it),
          mol = WORD.read(it)
        ),
        WORD.read(it),
        WORD.read(it),
        WORD.read(it),
        WORD.read(it)
      )
    }
  }
}