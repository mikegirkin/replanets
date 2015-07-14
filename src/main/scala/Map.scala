	package replanets.common

case class Map(
	height: Int,
	width: Int,
	planets: Seq[Planet],
	storms: Seq[IonStorm]
)

case object Map {

	readFromXyplan(filename: String) = {
		scala.io.Source.fromFile(filename)
	}

}
