package replanets.model

import replanets.common.{BeamspecItem, EngspecItem, HullspecItem, TorpspecItem}

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
