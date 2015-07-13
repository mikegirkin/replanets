package replanets.common

case Minerals(
	ne: Int,
	tr: Int,
	du: Int,
	mo: Int
)

case class Planet(
	id: Int,
	x: Int,
	y: Int,
	name: String
)

case class PlanetStructures(
	mines: Int,
	factories: Int,
	defenses: Int
)

case class PlanetState(
	planetId: id,
	minedMinerals: Minerals,
	unminedMinerals: Minerals,
	deltaMinerals: Minerals,
	supplies: Int,
	money: Int
)

case class IonStorm(
	id: Int,
	x: Int,
	y: Int,
	force: Int,
	speed: Int,
	azimuth: Int
)

case class Map(
	height: Int,
	width: Int,
	planets: Seq[Planet],
	storms: Seq[IonStorm]
)


