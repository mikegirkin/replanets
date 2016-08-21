package replanets.common

import replanets.recipes.{ArrayRecipe, RecordRecipe, WORD}

case class BaseRecord(
  baseId: Short,
  owner: Short,
  defences: Short,
  damage: Short,
  engineTech: Short,
  hullsTech: Short,
  beamTech: Short,
  torpedoTech: Short,
  storedEngines: IndexedSeq[Int],
  storedHulls: IndexedSeq[Int],
  storedBeams: IndexedSeq[Int],
  storedLaunchers: IndexedSeq[Int],
  storedTorpedoes: IndexedSeq[Int],
  fightersNumber: Short,
  actionedShipId: Short,
  shipAction: Short,
  primaryOrder: Short,
  buildShipType: Short,
  engineType: Short,
  beamType: Short,
  beamCount: Short,
  launcherType: Short,
  launchersCount: Short
)

object BasesReader {

  val basesRecipe = RecordRecipe(
    it => {
      val record = BaseRecord(
        WORD.read(it),
        WORD.read(it),
        WORD.read(it),
        WORD.read(it),
        WORD.read(it),
        WORD.read(it),
        WORD.read(it),
        WORD.read(it),
        ArrayRecipe(9, WORD).read(it).map(_.toInt),
        ArrayRecipe(20, WORD).read(it).map(_.toInt),
        ArrayRecipe(10, WORD).read(it).map(_.toInt),
        ArrayRecipe(10, WORD).read(it).map(_.toInt),
        ArrayRecipe(10, WORD).read(it).map(_.toInt),
        WORD.read(it),
        WORD.read(it),
        WORD.read(it),
        WORD.read(it),
        WORD.read(it),
        WORD.read(it),
        WORD.read(it),
        WORD.read(it),
        WORD.read(it),
        WORD.read(it)
      )
      WORD.read(it)  //last value in the record should be ignored
      record
    }
  )

  def read(it: Iterator[Byte]): IndexedSeq[BaseRecord] = {
    val count = WORD.read(it)
    ArrayRecipe(count, basesRecipe).read(it)
  }
}
