package replanets.model

import java.nio.file.{Path, Paths}

import replanets.common.{Constants, IteratorExtensions, PlanetnmItem, XyplandatItem}

case class ClusterMap(
	height: Int,
	width: Int,
	planets: IndexedSeq[Planet]
)

object ClusterMap {

	import IteratorExtensions._

	def fromDirectory(path: Path): ClusterMap = {
		val xyplanData = readFromXyplan(path.resolve(Constants.xyplanFilename))
		val planetNames = PlanetnmItem.fromFile(path.resolve(Constants.planetnmFilename))
		ClusterMap(
			Constants.MapHeight,
			Constants.MapWidth,
			xyplanData.zipWithIndex.map { case (item, idx) => Planet(
				idx + 1,
				item.x,
				item.y,
				planetNames(idx).name
			)}
		)
	}

	def readFromXyplan(path: Path): IndexedSeq[XyplandatItem] = {
		val byteArray = java.nio.file.Files.readAllBytes(path)
		byteArray.iterator.read(XyplandatItem.recipe, Constants.NumberOfPlanets)
	}

}
