package replanets.common

import replanets.recipes.SpacePaddedString


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
}
