package replanets

import java.nio.file.Paths

import replanets.common._
import replanets.model.{ClusterMap, Game, Specs}

object ConsoleRunner {
  def run = {
    val testFilesDirectory = "/Users/mgirkin/proj/gmil/replanets/testfiles"
    val raceNamesFilename = "race.nm"
    val rstFilename = "Player1.RST"

    val game = Game.initFromDirectory(Paths.get(testFilesDirectory))
    println(game)
    println

    val rstReadResult = RstFileReader.read(Paths.get(testFilesDirectory, rstFilename))
    println(rstReadResult)
    println
  }
}