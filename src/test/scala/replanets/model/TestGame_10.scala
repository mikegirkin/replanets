package replanets.model

import java.nio.file.Paths

import replanets.common._

trait TestGame_10 {
  val raceId = RaceId(10)
  val directory = Paths.get("testfiles/testgame-10")
  val specs = Specs.fromDirectory(directory)(PHostFormulas, new Missions(raceId, PHost4))
  val gameDb = new GameDatabase(directory, raceId)

  def game = Game(directory)(gameDb)
}
