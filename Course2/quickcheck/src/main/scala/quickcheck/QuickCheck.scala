package quickcheck

import org.scalacheck.*
import Arbitrary.*
import Gen.*
import Prop.forAll

abstract class QuickCheckHeap extends Properties("Heap") with IntHeap:
  lazy val genHeap: Gen[H] = {
    for
      a <- arbitrary[Int]
      h <- oneOf(Gen.const(empty), genHeap)
    yield 
      insert(a, h)
  }

  given Arbitrary[H] = Arbitrary(genHeap)

  // for any heap, adding the minimal element, and then finding it, should return the element in question
  property("gen1") = forAll { (h: H) =>
    val m = if isEmpty(h) then 0 else findMin(h)
    findMin(insert(m, h)) == m
  }

  // adding a single element to an empty heap, and then removing this element, should yield the element in question
  property("min1") = forAll { (a: Int) =>
    val h = insert(a, empty)
    findMin(h) == a
  }

  //If you insert any two elements into an empty heap, finding the minimum of the resulting heap should get the smallest of the two elements back.
  property("min2") = forAll { (a: Int, b: Int) =>
    val h = insert(a, empty)
    val h2 = insert(b, h)
    findMin(h2) == {if a<b then a else b}
  }
  //If you insert an element into an empty heap, then delete the minimum, the resulting heap should be empty.
  property("del1") = forAll { (a: Int) =>
    val h = insert(a, empty)
    deleteMin(h) == empty
  }

  //Given any heap, you should get a sorted sequence of elements when continually finding and deleting minima. (Hint: recursion and helper functions are your friends.)
  property("sort1") = forAll { (h: H) =>
    def isSorted(h: H): Boolean = {
      val min = findMin(h)
      val newHeap = deleteMin(h)
      if isEmpty(newHeap) then true
      else if min > findMin(newHeap) then false
      else isSorted(newHeap)
    }
    if isEmpty(h) then true
    else isSorted(h)
  }

  //Finding a minimum of the melding of any two heaps should return a minimum of one or the other.
  property("meld1") = forAll { (h1: H, h2: H) =>
    var m = findMin(meld(h1, h2))
    if isEmpty(h1) && isEmpty(h2) then true
    else if isEmpty(h1) then m == findMin(h2)
    else if isEmpty(h2) then m == findMin(h1)
    else m == findMin(h1) || m == findMin(h2)
  }

  // Extra: Meld two heaps. Then remove min from 1 and insert it into 2, meld the results. Compare two melds.
  property("meld2") = forAll { (h1: H, h2: H) =>
    def compHeap(h1: H, h2: H): Boolean = {
      if (isEmpty(h1) && isEmpty(h2)) true
      else {
        if findMin(h1) != findMin(h2) then false
        else compHeap(deleteMin(h1), deleteMin(h2))
      }
    }
    var m1 = meld(h1, h2)
    var m2 = meld(deleteMin(h1), insert(findMin(h1), h2))
    compHeap(m1, m2)
  }
