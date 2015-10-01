package replanets.common

case class Minerals(
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
	planetId: Int,
	mineralsMined: Minerals,
	mineralsCore: Minerals,
	mineralsDensity: Minerals,
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
