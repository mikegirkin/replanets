package replanets.model

import java.nio.file.{Files, Path, Paths}

import replanets.common._
import replanets.model.Specs.getClass

case class Specs(
  beamSpecs: IndexedSeq[BeamspecItem],
  torpSpecs: IndexedSeq[TorpspecItem],
  engineSpecs: IndexedSeq[EngspecItem],
  hullSpecs: IndexedSeq[HullspecItem],
  raceHulls: HullAssignment
) {
  def getHull(raceId: Int, raceHullIdx: Int): HullspecItem = {
    val hullId = raceHulls.getRaceHullIds(raceId)(raceHullIdx)
    hullSpecs(hullId)
  }

  def isGravitonic(hullId: Int): Boolean = {
    Seq(43, 44, 45).contains(hullId) //TODO: hardcoded bells and whistles!!!
  }
}

object Specs {
  import ResourcesExtension._

  def fromDirectory(path: Path): Specs = {
    val beams = BeamspecItem.fromFile(getFromResourcesIfInexistent(path.resolve(Constants.beamspecFilename), "/files/beamspec.dat"))
    val torps = TorpspecItem.fromFile(getFromResourcesIfInexistent(path.resolve(Constants.torpspecFilename), "/files/torpspec.dat"))
    val engines = EngspecItem.fromFile(getFromResourcesIfInexistent(path.resolve(Constants.engspecFilename), "/files/engspec.dat"))
    val hulls = HullspecItem.fromFile(getFromResourcesIfInexistent(path.resolve(Constants.hullspecFileName), "/files/hullspec.dat"))
    val hullAssignment = HullAssignment.fromFile(getFromResourcesIfInexistent(path.resolve(Constants.hullsAssignmentFilename), "files/truehull.dat"))

    Specs(beams, torps, engines, hulls, hullAssignment)
  }
}
