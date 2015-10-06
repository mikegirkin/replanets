import java.nio.file.Paths

import replanets.common.{PlanetName, RaceName}

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
          .take(numberOfRaces * RaceName.longNameRecipe.size)
          .grouped(RaceName.longNameRecipe.size)
          .map(x => RaceName.longNameRecipe.read(x.iterator))
        val shortNames = byteArray
          .slice(
            numberOfRaces * RaceName.longNameRecipe.size,
            numberOfRaces * RaceName.longNameRecipe.size + numberOfRaces * RaceName.shortNameRecipe.size)
          .grouped(RaceName.shortNameRecipe.size)
          .map(x => RaceName.shortNameRecipe.read(x.iterator))
        val adjectiveNames = byteArray
          .slice(
            numberOfRaces * RaceName.longNameRecipe.size + numberOfRaces * RaceName.shortNameRecipe.size,
            numberOfRaces * RaceName.longNameRecipe.size + numberOfRaces * RaceName.shortNameRecipe.size + numberOfRaces * RaceName.adjectiveRecipe.size)
          .grouped(RaceName.adjectiveRecipe.size)
          .map(x => RaceName.adjectiveRecipe.read(x.iterator))
        longNames zip shortNames zip adjectiveNames map { case ((ln, sn), adj) => RaceName(ln, sn, adj) } toList
    }

    def readPlanetnames(filename: String) = {
        val byteArray = java.nio.file.Files.readAllBytes(Paths.get(filename))
        byteArray.grouped(PlanetName.recipe.size).map { record =>
            val iter = record.iterator
            PlanetName.recipe.read(iter)
        }.toList
    }
}

