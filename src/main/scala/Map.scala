package replanets.common

import java.nio.{ByteOrder, ByteBuffer}
import java.nio.file.Paths

case class Map(
	height: Int,
	width: Int,
	planets: Seq[Planet],
	storms: Seq[IonStorm]
)

case object Map {

	case class XYPlanDatRecord(
		x: Short,
		y: Short,
		owner: Short
	)

	def readFromXyplan(filename: String): Array[XYPlanDatRecord] = {
		def readShort(source: Array[Byte]) =
			ByteBuffer.wrap(source).order(ByteOrder.LITTLE_ENDIAN).getShort

		val filename = "/Users/mgirkin/proj/gmil/replanets/testfiles/xyplan.dat"
		val byteArray = java.nio.file.Files.readAllBytes(Paths.get(filename))
		byteArray.grouped(6).map { record =>
			XYPlanDatRecord(
				readShort(record.slice(0, 2)),
				readShort(record.slice(2, 4)),
				readShort(record.slice(4, 6))
			)
		}.toArray
	}

}
