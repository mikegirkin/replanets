import java.nio.file.Paths

import replanets.common.{BeamspecItem, EngspecItem, TorpspecItem, PlanetnmItem, RacenmItem}

object Runner {
  def main(args: Array[String]) = {
    val xyplanFilename = "/Users/mgirkin/proj/gmil/replanets/testfiles/xyplan.dat"
    val planetsNamesFilename = "/Users/mgirkin/proj/gmil/replanets/testfiles/planet.nm"
    val raceNamesFilename = "/Users/mgirkin/proj/gmil/replanets/testfiles/race.nm"
    val torpspecFilename = "/Users/mgirkin/proj/gmil/replanets/testfiles/torpspec.dat"
    val engspecFilename = "/Users/mgirkin/proj/gmil/replanets/testfiles/engspec.dat"
    val beamspecFilename = "/Users/mgirkin/proj/gmil/replanets/testfiles/beamspec.dat"

    val map = replanets.common.Map.readFromXyplan(xyplanFilename)
    println(map.toList)
    println
    val races = readRacenames(raceNamesFilename)
    println(races)
    println
    val planets = readPlanetnames(planetsNamesFilename)
    println(planets)
    println
    val toprs = readTorpspecs(torpspecFilename)
    println(toprs)
    println
    val engines = readEngspecs(engspecFilename)
    println(engines)
    println
    val beams = readBeamspecs(beamspecFilename)
    println(beams)
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
      longNames zip shortNames zip adjectiveNames map { case ((ln, sn), adj) => RacenmItem(ln, sn, adj) } toIndexedSeq
  }

  def readPlanetnames(filename: String) = PlanetnmItem.recipe.readFromFile(filename).toIndexedSeq

  def readTorpspecs = TorpspecItem.readFromFile _

  def readEngspecs(filename: String) = EngspecItem.recipe.readFromFile(filename, Some(9)).toIndexedSeq

  def readBeamspecs(filename: String) = BeamspecItem.recipe.readFromFile(filename).toIndexedSeq
}

