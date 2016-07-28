package replanets.common

import java.nio.file.Path

import replanets.recipes.{ArrayRecipe, SpacePaddedString}

case class RacenmItem(
  longName: String,
  shortname: String,
  adjective: String
  )

object RacenmItem {
  private val longNameSize = 30
  private val shortNameSize = 20
  private val adjectiveSize = 12

  val longNameRecipe = SpacePaddedString(longNameSize)
  val shortNameRecipe = SpacePaddedString(shortNameSize)
  val adjectiveRecipe = SpacePaddedString(adjectiveSize)

  def fromFile(file: Path): IndexedSeq[RacenmItem] = {
    val it = java.nio.file.Files.readAllBytes(file).iterator
    val longNames = ArrayRecipe(Constants.NumberOfRaces, longNameRecipe).read(it)
    val shortNames = ArrayRecipe(Constants.NumberOfRaces, shortNameRecipe).read(it)
    val adjectives = ArrayRecipe(Constants.NumberOfRaces, adjectiveRecipe).read(it)
    (longNames, shortNames, adjectives).zipped
      .map(RacenmItem.apply)
  }
}
