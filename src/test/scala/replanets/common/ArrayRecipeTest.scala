package replanets.common

import java.nio.{ByteBuffer, ByteOrder}

import org.scalatest.{Matchers, WordSpec}
import replanets.recipes.{ArrayRecipe, DWORD}

/**
  * Created by mgirkin on 26/07/2016.
  */
class ArrayRecipeTest extends WordSpec with Matchers {
  "Array recipe could read dwords" in {
    val source = Array(10, 20, 35, 50, 70, 117, 2374)
    val bb = ByteBuffer.allocate(4 * source.length)
    bb.order(ByteOrder.LITTLE_ENDIAN)
    source.indices.foreach(idx => bb.putInt(source(idx)))
    val iterator = bb.array().iterator

    val recipe = new ArrayRecipe(7)(DWORD)

    val result = recipe.read(iterator)

    result should contain theSameElementsInOrderAs source
  }
}