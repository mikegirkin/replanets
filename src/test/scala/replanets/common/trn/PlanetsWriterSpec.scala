package replanets.common.trn

import org.scalatest.{Matchers, WordSpec}

/**
  * Created by mgirkin on 13/08/2016.
  */
class PlanetsWriterSpec extends WordSpec with Matchers {
  import replanets.model.trn.PlanetsWriter._

  "Short is written as little endian" in {
    val bytes = 0x0145.toShort.toBytes

    bytes.toSeq should contain theSameElementsInOrderAs Seq(0x45, 0x01)
  }

  "Int is written as little endian" in {
    val bytes = 0x10203040.toBytes

    bytes.toSeq should contain theSameElementsInOrderAs Seq(0x40, 0x30, 0x20, 0x10)
  }

  "Strings is written padded correctly" in {
    val bytes = "Anonym".toBytes(10)

    bytes.toSeq should contain theSameElementsInOrderAs Seq(0x41, 0x6E, 0x6F, 0x6E, 0x79, 0x6D, 0x20, 0x20, 0x20, 0x20)
  }
}
