package replanets.common

import replanets.recipes.{ArrayRecipe, RecordRecipe, WORD}

/**
  * Created by mgirkin on 27/07/2016.
  */

case class BaseRecord(
  baseId: Short,
  owner: Short,
  defences: Short,
  damage: Short,
  engineTech: Short,
  hullsTech: Short,
  beamTech: Short,
  torpedoTech: Short,
  storedEngines: IndexedSeq[Short],
  storedHulls: IndexedSeq[Short],
  storedBeams: IndexedSeq[Short],
  storedLaunchers: IndexedSeq[Short],
  storedTorpedoes: IndexedSeq[Short],
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
    it => BaseRecord(
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      ArrayRecipe(9, WORD).read(it),
      ArrayRecipe(20, WORD).read(it),
      ArrayRecipe(10, WORD).read(it),
      ArrayRecipe(10, WORD).read(it),
      ArrayRecipe(10, WORD).read(it),
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
  )

  def read(it: Iterator[Byte]): IndexedSeq[BaseRecord] = {
    val count = WORD.read(it)
    ArrayRecipe(count, basesRecipe).read(it)
  }
}
