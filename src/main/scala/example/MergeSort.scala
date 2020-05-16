package example

object MergeSort {

  /**
   * The number of micro operation inside of this function is
   *
   * ```
   * min(|a|, |b|) + rest(|a|) + rest(|b|) => |a| + |b|
   * ```
   *
   * Number of operations inside the while loop is four, plus two operation of initialization.
   *
   * ```
   * (|a| + |b|) * 4 + 2 <= 6n (see the text book).
   * ```
   */
  def merge[A, F[A] <: Iterable[A]](a: F[A], b: F[A])(implicit ord: Ordering[A]): Vector[A] = {
    val res = Vector.newBuilder[A]

    val aIter = a.iterator.buffered
    val bIter = b.iterator.buffered

    while (aIter.hasNext && bIter.hasNext) {
      if (ord.lteq(aIter.head, bIter.head)) {
        res.addOne(aIter.next())
      } else {
        res.addOne(bIter.next())
      }
    }

    if (aIter.hasNext) {
      res.addAll(aIter)
    }

    if (bIter.hasNext) {
      res.addAll(bIter)
    }

    res.result()
  }

  def sort[A, F[A] <: Iterable[A]](value: F[A])(implicit ord: Ordering[A]): Vector[A] = {
    val valueSize = value.size
    if (valueSize == 0) {
      Vector.empty
    } else if (valueSize == 1) {
      value.toVector
    } else if (valueSize == 2) {
      val head = value.head
      val last = value.last
      if (ord.lteq(head, last)) {
        Vector(head, last)
      } else {
        Vector(last, head)
      }
    } else {
      val halfSize = valueSize / 2
      val (a, b) = value.splitAt(halfSize)

      val aSorted = sort(a)
      val bSorted = sort(b)

      merge(aSorted, bSorted)
    }
  }

}
