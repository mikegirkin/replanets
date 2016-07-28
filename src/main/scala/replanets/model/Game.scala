package replanets.model

import java.nio.file.Path

import replanets.common.{Constants, RacenmItem}

/**
  * Created by mgirkin on 27/07/2016.
  */

case class Game(
  name: String,
  dataPath: Path,
  races: IndexedSeq[RacenmItem],
  map: ClusterMap,
  specs: Specs
) {
  override def toString: String = {
    String.join(
      sys.props("line.separator") + sys.props("line.separator"),
      name.toString, dataPath.toString, races.toString, map.toString, specs.toString)
  }
}

object Game {
  def initFromDirectory(directory: Path): Game = {
    val map = ClusterMap.fromDirectory(directory)
    val specs = Specs.fromDirectory(directory)
    val races = RacenmItem.fromFile(directory.resolve(Constants.racenmFilename))

    Game("Test game", directory, races, map, specs)
  }
}