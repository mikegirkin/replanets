package replanets.model

import java.nio.file.Path

import replanets.common._

/**
  * Created by mgirkin on 27/07/2016.
  */
case class Specs(
  beamSpecs: IndexedSeq[BeamspecItem],
  torpSpecs: IndexedSeq[TorpspecItem],
  engineSpecs: IndexedSeq[EngspecItem],
  hullSpecs: IndexedSeq[HullspecItem],
  raceHulls: HullAssignment
) {
  def getHull(raceId: Int, raceHullId: Int): HullspecItem = {
    val hullId = raceHulls.getRaceHullIds(raceId)(raceHullId)
    hullSpecs(hullId)
  }

  def isGravitonic(hullId: Int): Boolean = {
    Seq(43, 44, 45).contains(hullId) //TODO: hardcoded bells and whistles!!!
  }
}

object Specs {
  def fromDirectory(path: Path): Specs = {
    val beams = BeamspecItem.fromFile(path.resolve(Constants.beamspecFilename))
    val torps = TorpspecItem.fromFile(path.resolve(Constants.torpspecFilename))
    val engines = EngspecItem.fromFile(path.resolve(Constants.engspecFilename))
    val hulls = HullspecItem.fromFile(path.resolve(Constants.hullspecFileName))
    val hullAssignment = HullAssignment.fromFile(path.resolve(Constants.hullsAssignmentFilename))

    Specs(beams, torps, engines, hulls, hullAssignment)
  }
}
