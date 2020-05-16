package example

object MergeSort {

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

}
