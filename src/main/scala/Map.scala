package replanets.common

import java.nio.{ByteOrder, ByteBuffer}
import java.nio.file.Paths

import replanets.recipes._

case class Map(
	height: Int,
	width: Int,
	planets: Seq[Planet],
	storms: Seq[IonStorm]
)

case class PlanetName(
	name: String
)

object PlanetName {
	val raceNameSize = 20
	val recipe = RecordRecipe(
		SpacePaddedString(raceNameSize)
	)(PlanetName.apply)
}

case class RaceName(
	longName: String,
	shortname: String,
  adjective: String
)

object RaceName {
	private val longNameSize = 30
	private val shortNameSize = 20
	private val adjectiveSize = 12

	val longNameRecipe = SpacePaddedString(longNameSize)
	val shortNameRecipe = SpacePaddedString(shortNameSize)
	val adjectiveRecipe = SpacePaddedString(adjectiveSize)
}

object Map {


	case class XYPlanDatRecord(
		x: Short,
		y: Short,
		owner: Short
	)

	def readFromXyplan(filename: String): Array[XYPlanDatRecord] = {
		val filename = "/Users/mgirkin/proj/gmil/replanets/testfiles/xyplan.dat"
		val byteArray = java.nio.file.Files.readAllBytes(Paths.get(filename))
		val xyPlanRecordRecipe = RecordRecipe(WORD, WORD, WORD)(XYPlanDatRecord.apply)
		byteArray.grouped(xyPlanRecordRecipe.size).map { record =>
			val iter = record.iterator
			xyPlanRecordRecipe.read(iter)
		}.toArray
	}

}
