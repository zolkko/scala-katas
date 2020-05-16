package example

import org.scalatest.{FlatSpec, Matchers}

class MergeSortSpec extends FlatSpec with Matchers {

  "merge" should "support empty inputs" in {
    Array(1, 2, 3).iterator
    val result = MergeSort.merge(List.empty[Int], List.empty[Int])
    result should be (empty)
  }

  it should "return the left list of the right list is empty" in {
    val result = MergeSort.merge(Vector(1, 2, 3), Vector.empty)
    result should contain theSameElementsInOrderAs Vector(1, 2, 3)
  }

  it should "return the right list if the left list is empty" in {
    val result = MergeSort.merge(Vector.empty, Vector(3, 4, 5))
    result should contain theSameElementsInOrderAs Vector(3, 4, 5)
  }

  it should "merge trivial lists" in {
    val result1 = MergeSort.merge(Vector(1), Vector(3))
    result1 should contain theSameElementsInOrderAs Vector(1, 3)

    val result2 = MergeSort.merge(Vector(10), Vector(5))
    result2 should contain theSameElementsInOrderAs Vector(5, 10)
  }

  it should "merge non-trivial lists" in {
    val result = MergeSort.merge(Vector(1, 3, 5), Vector(2, 4, 6))
    result should contain theSameElementsInOrderAs Vector(1, 2, 3, 4, 5, 6)
  }

  it should "merge lists of different size" in {
    val result = MergeSort.merge(Vector(1, 3), Vector(2, 4, 5))
    result should contain theSameElementsInOrderAs Vector(1, 2, 3, 4, 5)
  }

  it should "merge lists with similar elements" in {
    val result = MergeSort.merge(Seq(1, 3, 3), Seq(2, 3, 4, 4, 5))
    result should contain theSameElementsInOrderAs Seq(1, 2, 3, 3, 3, 4, 4, 5)
  }

}
