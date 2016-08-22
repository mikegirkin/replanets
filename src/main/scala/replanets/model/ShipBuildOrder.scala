package replanets.model

import replanets.common._

case class BeamsOrder(
  spec: BeamspecItem,
  count: Int
)

case class LaunchersOrder(
  spec: TorpspecItem,
  count: Int
)

case class ShipBuildOrder(
  hull: HullspecItem,
  engine: EngspecItem,
  beams: Option[BeamsOrder],
  launchers: Option[LaunchersOrder]
)

object ShipBuildOrder {
  def getBuildOrder(specs: Specs)(
    hullId: HullId, engineId: EngineId,
    beamId: BeamId, beamCount: Int,
    launcherId: LauncherId, launcherCount: Int
  ) = ShipBuildOrder(
    specs.hullSpecs.find(_.id == hullId).get,
    specs.engineSpecs.find(_.id == engineId).get,
    if(beamId != BeamId.Nothing) Some(BeamsOrder(specs.beamSpecs.find(_.id == beamId).get, beamCount)) else None,
    if(launcherId != LauncherId.Nothing) Some(LaunchersOrder(specs.torpSpecs.find(_.id == launcherId).get, launcherCount)) else None
  )
}
