package replanets.recipes

object RecordRecipe {
  def apply[R, A1](a1: BinaryReadRecipe[A1])(apply: (A1) => R) =
    new BinaryReadRecipe[R] {
      val size = a1.size

      override def read(source: Iterator[Byte]): R =
        apply(
          a1.read(source)
        )
    }

  def apply[R, A1, A2, A3](a1: BinaryReadRecipe[A1], a2: BinaryReadRecipe[A2], a3: BinaryReadRecipe[A3])(apply: (A1, A2, A3) => R) =
    new BinaryReadRecipe[R] {
      val size = a1.size + a2.size + a3.size

      override def read(source: Iterator[Byte]): R =
        apply(
          a1.read(source),
          a2.read(source),
          a3.read(source)
        )
    }

  def apply[R, A1, A2, A3, A4, A5, A6, A7](
    a1: BinaryReadRecipe[A1], a2: BinaryReadRecipe[A2], a3: BinaryReadRecipe[A3],
    a4: BinaryReadRecipe[A4], a5: BinaryReadRecipe[A5], a6: BinaryReadRecipe[A6],
    a7: BinaryReadRecipe[A7])(apply: (A1, A2, A3, A4, A5, A6, A7) => R) =
    new BinaryReadRecipe[R] {
      val size = List(a1, a2, a3, a4, a5, a6, a7).map(_.size).sum

      override def read(source: Iterator[Byte]): R =
        apply(
          a1.read(source),
          a2.read(source),
          a3.read(source),
          a4.read(source),
          a5.read(source),
          a6.read(source),
          a7.read(source)
        )
    }

  def apply[R, A1, A2, A3, A4, A5, A6, A7, A8, A9](
      a1: BinaryReadRecipe[A1], a2: BinaryReadRecipe[A2], a3: BinaryReadRecipe[A3],
      a4: BinaryReadRecipe[A4], a5: BinaryReadRecipe[A5], a6: BinaryReadRecipe[A6],
      a7: BinaryReadRecipe[A7], a8: BinaryReadRecipe[A8], a9: BinaryReadRecipe[A9]
    )(apply: (A1, A2, A3, A4, A5, A6, A7, A8, A9) => R) = new BinaryReadRecipe[R] {
      val size = List(a1, a2, a3, a4, a5, a6, a7, a8, a9).map(_.size).sum

      override def read(source: Iterator[Byte]): R =
        apply(
          a1.read(source),
          a2.read(source),
          a3.read(source),
          a4.read(source),
          a5.read(source),
          a6.read(source),
          a7.read(source),
          a8.read(source),
          a9.read(source)
        )
  }

  def apply[R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10](
      a1: BinaryReadRecipe[A1], a2: BinaryReadRecipe[A2], a3: BinaryReadRecipe[A3],
      a4: BinaryReadRecipe[A4], a5: BinaryReadRecipe[A5], a6: BinaryReadRecipe[A6],
      a7: BinaryReadRecipe[A7], a8: BinaryReadRecipe[A8], a9: BinaryReadRecipe[A9],
      a10: BinaryReadRecipe[A10]
    )(apply: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10) => R) = new BinaryReadRecipe[R] {
    val size = List(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10).map(_.size).sum

    override def read(source: Iterator[Byte]): R =
      apply(
        a1.read(source),
        a2.read(source),
        a3.read(source),
        a4.read(source),
        a5.read(source),
        a6.read(source),
        a7.read(source),
        a8.read(source),
        a9.read(source),
        a10.read(source)
      )
  }

  def apply[R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16](
    a1: BinaryReadRecipe[A1], a2: BinaryReadRecipe[A2], a3: BinaryReadRecipe[A3],
    a4: BinaryReadRecipe[A4], a5: BinaryReadRecipe[A5], a6: BinaryReadRecipe[A6],
    a7: BinaryReadRecipe[A7], a8: BinaryReadRecipe[A8], a9: BinaryReadRecipe[A9],
    a10: BinaryReadRecipe[A10], a11: BinaryReadRecipe[A11], a12: BinaryReadRecipe[A12],
    a13: BinaryReadRecipe[A13], a14: BinaryReadRecipe[A14], a15: BinaryReadRecipe[A15],
    a16: BinaryReadRecipe[A16]
    )(apply: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16) => R) = new BinaryReadRecipe[R] {
    val size = List(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16).map(_.size).sum

    override def read(source: Iterator[Byte]): R =
      apply(
        a1.read(source),
        a2.read(source),
        a3.read(source),
        a4.read(source),
        a5.read(source),
        a6.read(source),
        a7.read(source),
        a8.read(source),
        a9.read(source),
        a10.read(source),
        a11.read(source),
        a12.read(source),
        a13.read(source),
        a14.read(source),
        a15.read(source),
        a16.read(source)
      )
  }
}
