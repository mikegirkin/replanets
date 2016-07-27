package replanets

import java.nio.file.Paths

import replanets.common.XyplandatItem

case class Map(
	height: Int,
	width: Int,
	planets: Seq[Planet],
	storms: Seq[IonStorm]
)

object Map {

	def readFromXyplan(filename: String): Array[XyplandatItem] = {
		val byteArray = java.nio.file.Files.readAllBytes(Paths.get(filename))
		byteArray.grouped(XyplandatItem.recipe.size).map { record =>
			val iter = record.iterator
			XyplandatItem.recipe.read(iter)
		}.toArray
	}

}
