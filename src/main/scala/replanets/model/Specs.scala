package replanets.model

import java.nio.file.Path

import replanets.common._

case class Specs(
  formulas: Formulas,
  missions: Missions,
  map: ClusterMap,
  beamSpecs: IndexedSeq[BeamspecItem],
  torpSpecs: IndexedSeq[TorpspecItem],
  engineSpecs: IndexedSeq[EngspecItem],
  hullSpecs: IndexedSeq[HullspecItem],
  raceHulls: HullAssignment,
  hullFunctions: Map[HullId, Set[HullFunc]]
) {
  def getRaceHulls(race: RaceId): IndexedSeq[HullspecItem] = {
    val hullIndexes = raceHulls.getRaceHullIds(race)
    hullIndexes.map(idx => hullSpecs(idx - 1))
  }

  def isGravitonic(hullId: HullId): Boolean = {
    hullFunctions.get(hullId)
      .exists(hf => hf.contains(Gravitonic))
  }

}

object Specs {
  import ResourcesExtension._

  def fromDirectory(path: Path)(formulas: Formulas, availableMissions: Missions): Specs = {
    val beams = BeamspecItem.fromFile(getFromResourcesIfInexistent(path.resolve(Constants.beamspecFilename), "/files/beamspec.dat"))
    val torps = TorpspecItem.fromFile(getFromResourcesIfInexistent(path.resolve(Constants.torpspecFilename), "/files/torpspec.dat"))
    val engines = EngspecItem.fromFile(getFromResourcesIfInexistent(path.resolve(Constants.engspecFilename), "/files/engspec.dat"))
    val hullfuncs = Constants.thostHullFunctions
    val hulls = HullspecItem.fromFile(getFromResourcesIfInexistent(path.resolve(Constants.hullspecFileName), "/files/hullspec.dat"), hullfuncs)
    val hullAssignment = HullAssignment.fromFile(getFromResourcesIfInexistent(path.resolve(Constants.hullsAssignmentFilename), "files/truehull.dat"))
    val map = ClusterMap.fromDirectory(path)

    Specs(formulas, availableMissions, map, beams, torps, engines, hulls, hullAssignment, hullfuncs)
  }
}
