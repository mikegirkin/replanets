package replanets.model

import replanets.common.Cost

case class ShipCost(
  hullCost: Cost,
  enginesCost: Cost,
  beamsCost: Cost,
  launcherCost: Cost,
  techCost: Cost
) {
  def total: Cost = hullCost.add(enginesCost).add(beamsCost).add(launcherCost).add(techCost)
}
