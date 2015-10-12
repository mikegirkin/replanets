import java.nio.file.Paths

import replanets.common.{PlanetnmItem, PlanetnmItem$, RacenmItem}

object Runner {
    def main(args: Array[String]) = {
        val xyplanFilename = "/Users/mgirkin/proj/gmil/replanets/testfiles/xyplan.dat"
        val planetsNamesFilename = "/Users/mgirkin/proj/gmil/replanets/testfiles/planet.nm"
        val raceNamesFilename = "/Users/mgirkin/proj/gmil/replanets/testfiles/race.nm"

        val map = replanets.common.Map.readFromXyplan(xyplanFilename)
        println(map.toList)
        println
        val races = readRacenames(raceNamesFilename)
        println(races)
        println
        val planets = readPlanetnames(planetsNamesFilename)
        println(planets)
        println
    }

    def readRacenames(filename: String) = {
        val byteArray = java.nio.file.Files.readAllBytes(Paths.get(filename))
        val numberOfRaces = 11

        val longNames = byteArray
          .take(numberOfRaces * RacenmItem.longNameRecipe.size)
          .grouped(RacenmItem.longNameRecipe.size)
          .map(x => RacenmItem.longNameRecipe.read(x.iterator))
        val shortNames = byteArray
          .slice(
            numberOfRaces * RacenmItem.longNameRecipe.size,
            numberOfRaces * RacenmItem.longNameRecipe.size + numberOfRaces * RacenmItem.shortNameRecipe.size)
          .grouped(RacenmItem.shortNameRecipe.size)
          .map(x => RacenmItem.shortNameRecipe.read(x.iterator))
        val adjectiveNames = byteArray
          .slice(
            numberOfRaces * RacenmItem.longNameRecipe.size + numberOfRaces * RacenmItem.shortNameRecipe.size,
            numberOfRaces * RacenmItem.longNameRecipe.size + numberOfRaces * RacenmItem.shortNameRecipe.size + numberOfRaces * RacenmItem.adjectiveRecipe.size)
          .grouped(RacenmItem.adjectiveRecipe.size)
          .map(x => RacenmItem.adjectiveRecipe.read(x.iterator))
        longNames zip shortNames zip adjectiveNames map { case ((ln, sn), adj) => RacenmItem(ln, sn, adj) } toList
    }

    def readPlanetnames(filename: String) = {
        val byteArray = java.nio.file.Files.readAllBytes(Paths.get(filename))
        byteArray.grouped(PlanetnmItem.recipe.size).map { record =>
            val iter = record.iterator
            PlanetnmItem.recipe.read(iter)
        }.toList
    }
}

