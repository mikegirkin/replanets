package replanets.model

import java.nio.file.Paths

import replanets.common.{GameDatabase, Missions, RaceId, THost}

trait TestGame_1 {
  val raceId = RaceId(1)
  val directory = Paths.get("testfiles/testgame-1")
  val specs = Specs.fromDirectory(directory)(THostFormulas, new Missions(RaceId(1), THost))
  val gameDb = new GameDatabase(directory, raceId)

  def game = Game(directory)(gameDb)
}