package replanets.common

import java.nio.file.Path

import replanets.recipes.{RecordRecipe, SpacePaddedString}

case class PlanetnmItem(
  name: String
)

object PlanetnmItem {
  val raceNameSize = 20
  val recipe = RecordRecipe(
    SpacePaddedString(raceNameSize)
  )(PlanetnmItem.apply)

  def fromFile(path: Path) = {
    recipe.readFromFile(path)
  }
}


