package replanets.model

import java.nio.file.{Path, Paths}

import replanets.common.{Constants, PlanetnmItem, XyplandatItem}

case class ClusterMap(
	height: Int,
	width: Int,
	planets: IndexedSeq[Planet]
)

object ClusterMap {

	def fromDirectory(path: Path): ClusterMap = {
		val xyplanData = readFromXyplan(path.resolve(Constants.xyplanFilename))
		val planetNames = PlanetnmItem.fromFile(path.resolve(Constants.planetnmFilename))
		ClusterMap(
			4000,
			4000,
			xyplanData.zipWithIndex.map { case (item, idx) => Planet(
				idx + 1,
				item.x,
				item.y,
				planetNames(idx).name
			)}
		)
	}

	def readFromXyplan(path: Path): Array[XyplandatItem] = {
		val byteArray = java.nio.file.Files.readAllBytes(path)
		byteArray.grouped(XyplandatItem.recipe.size).map { record =>
			val iter = record.iterator
			XyplandatItem.recipe.read(iter)
		}.toArray
	}

}
