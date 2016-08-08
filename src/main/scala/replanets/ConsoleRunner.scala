package replanets

import java.nio.file.Paths

import replanets.common._
import replanets.model.Game

object ConsoleRunner {
  def run = {
    val testFilesDirectory = "/Users/mgirkin/proj/gmil/replanets/testfiles"
    val path = Paths.get(testFilesDirectory)

    val db = new GameDatabase(path)
    val game = Game(path)(db)
    println(game)
    println
  }
}