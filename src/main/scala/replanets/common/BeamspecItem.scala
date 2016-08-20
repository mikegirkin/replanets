package replanets.common

import java.nio.file.{Files, Path}

import replanets.recipes._

case class BeamspecItem(
  id: BeamId,
  name: String,
  cost: Cost,
  mass: Short,
  techLevel: Short,
  killValue: Short,
  damageValue: Short
)

object BeamspecItem {
  
  val beamNameLength = 20

  def fromFile(file: Path): IndexedSeq[BeamspecItem] = {
    val it = Files.readAllBytes(file).iterator
    (1 to Constants.BeamsInBeamspec).map { idx =>
      BeamspecItem(
        BeamId(idx),
        SpacePaddedString(beamNameLength).read(it),
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
