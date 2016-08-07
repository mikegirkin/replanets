package replanets.model

import java.nio.file.Path

import replanets.common.{Constants, HullspecItem, RacenmItem, RstFileReader}

import scala.collection.mutable

case class Game(
  name: String,
  dataPath: Path,
  playingRace: Int,
  races: IndexedSeq[RacenmItem],
  map: ClusterMap,
  specs: Specs
) {

  val formulas: Formulas = THostFormulas
  val turns = mutable.Map[Int, TurnInfo]()

  override def toString: String = {
    String.join(
      sys.props("line.separator") + sys.props("line.separator"),
      name.toString, dataPath.toString, races.toString, map.toString, specs.toString)
  }

  def processRstFile(rstFile: Path): Game = {
    val rst = RstFileReader.read(rstFile)
    val ti = TurnInfo(ReceivedState(Map(rst.generalInfo.playerId.toInt -> rst)))
    turns += (rst.generalInfo.turnNumber.toInt -> ti)
    this
  }

  def turnSeverData(turn: Int) = {
    turns(turn).serverReceiveState.rstFiles(playingRace)
  }

  def playingRaceId = playingRace - 1
}

object Game {
  def initFromDirectory(directory: Path): Game = {
    val map = ClusterMap.fromDirectory(directory)
    val specs = Specs.fromDirectory(directory)
    val races = RacenmItem.fromFile(directory.resolve(Constants.racenmFilename))

    Game("Test game", directory, 1, races, map, specs)
  }
}