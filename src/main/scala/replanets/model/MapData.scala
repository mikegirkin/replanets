package replanets.model

import replanets.common.ObjectWithCoords

case class PlanetMapData(
	id: Short,
	x: Short,
	y: Short,
	name: String
) extends ObjectWithCoords