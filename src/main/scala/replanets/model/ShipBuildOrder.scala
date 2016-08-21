package replanets.model

import replanets.common.{BeamspecItem, EngspecItem, HullspecItem, TorpspecItem}

case class ShipBuildOrder(
  hull: HullspecItem,
  engine: EngspecItem,
  beam: BeamspecItem,
  beamCount: Int,
  launchers: TorpspecItem,
  launcherCount: Int
)
