package replanets.common

import java.nio.{ByteOrder, ByteBuffer}
import java.nio.file.Paths

case class Map(
	height: Int,
	width: Int,
	planets: Seq[Planet],
	storms: Seq[IonStorm]
)

trait BinaryReadRecipe[T] {
	def read(source: Iterator[Byte]): T
	val size: Int
}

object WORD extends BinaryReadRecipe[Short] {
	val size = 2

	def read(source: Iterator[Byte]): Short =
		ByteBuffer.wrap(Array(source.next(), source.next())).order(ByteOrder.LITTLE_ENDIAN).getShort
}

object RecordRecipe {
	def apply[R, A1, A2, A3](a1: BinaryReadRecipe[A1], a2: BinaryReadRecipe[A2], a3: BinaryReadRecipe[A3])(apply: (A1, A2, A3) => R) = new BinaryReadRecipe[R] {
		val size = a1.size + a2.size + a3.size

		override def read(source: Iterator[Byte]): R =
			apply(
				a1.read(source),
				a2.read(source),
				a3.read(source)
			)
	}
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
