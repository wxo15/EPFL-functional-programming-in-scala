package kmeans

import java.util.concurrent.*
import scala.collection.{mutable, Map, Seq}
import scala.collection.parallel.{ParMap, ParSeq}
import scala.collection.parallel.CollectionConverters.*
import scala.math.*

class KMeansSuite extends munit.FunSuite:
  object KM extends KMeans
  import KM.*

  def checkParClassify(points: ParSeq[Point], means: ParSeq[Point], expected: ParMap[Point, ParSeq[Point]]): Unit =
    assertEquals(classify(points, means), expected, s"classify($points, $means) should equal to $expected")

  def checkParKMeans(points: ParSeq[Point], means: ParSeq[Point], eta: Double, expected: ParSeq[Point]): Unit =
    assertEquals(kMeans(points, means, eta), expected, s"kMeans($points, $means, $eta) should equal to $expected")

  test("'classify' should work for empty 'points' and empty 'means'") {
    val points: ParSeq[Point] = IndexedSeq().par
    val means: ParSeq[Point] = IndexedSeq().par
    val expected = ParMap[Point, ParSeq[Point]]()
    checkParClassify(points, means, expected)
  }

  test("'classify' should work for empty 'points' and 'means' == ParSeq(Point(1,1,1))") {
    val points: ParSeq[Point] = IndexedSeq().par
    val mean = new Point(1, 1, 1)
    val means: ParSeq[Point] = IndexedSeq(mean).par
    val expected = ParMap[Point, ParSeq[Point]]((mean, ParSeq()))
    checkParClassify(points, means, expected)
  }

  test("'classify' should work for 'points' == ParSeq((1, 1, 0), (1, -1, 0), (-1, 1, 0), (-1, -1, 0)) and 'means' == ParSeq((0, 0, 0))") {
    val p1 = new Point(1, 1, 0)
    val p2 = new Point(1, -1, 0)
    val p3 = new Point(-1, 1, 0)
    val p4 = new Point(-1, -1, 0)
    val points: ParSeq[Point] = IndexedSeq(p1, p2, p3, p4).par
    val mean = new Point(0, 0, 0)
    val means: ParSeq[Point] = IndexedSeq(mean).par
    val expected = ParMap((mean, ParSeq(p1, p2, p3, p4)))
    checkParClassify(points, means, expected)
  }

  test("'classify' should work for 'points' == ParSeq((1, 1, 0), (1, -1, 0), (-1, 1, 0), (-1, -1, 0)) and 'means' == ParSeq((1, 0, 0), (-1, 0, 0))") {
    val p1 = new Point(1, 1, 0)
    val p2 = new Point(1, -1, 0)
    val p3 = new Point(-1, 1, 0)
    val p4 = new Point(-1, -1, 0)
    val points: ParSeq[Point] = IndexedSeq(p1, p2, p3, p4).par
    val mean1 = new Point(1, 0, 0)
    val mean2 = new Point(-1, 0, 0)
    val means: ParSeq[Point] = IndexedSeq(mean1, mean2).par
    val expected = ParMap((mean1, ParSeq(p1, p2)), (mean2, ParSeq(p3, p4)))
    checkParClassify(points, means, expected)
  }

  test("'classify with data parallelism should work for empty 'points' and empty 'means'") {
    val points: ParSeq[Point] = IndexedSeq().par
    val means: ParSeq[Point] = IndexedSeq().par
    val expected = ParMap[Point,ParSeq[Point]]()
    checkParClassify(points, means, expected)
  }

  test("'converged' should work for empty oldMeans and empty newMeans") {
    assert(converged(0.1, ParSeq(), ParSeq()),
      s"converged(Vector(), $Vector()) should equal to true")
  }
/*
  test("kMeans") {
    val p1 = new Point(0.0, 0.0, 1.0)
    val p2 = new Point(0.0, 0.0, -1.0)
    val p3 = new Point(0.0, 1.0, 0.0)
    val p4 = new Point(0.0, 10.0, 0.0)
    val points: ParSeq[Point] = IndexedSeq(p1, p2, p3, p4).par
    val mean1 = new Point(0, -1, 0)
    val mean2 = new Point(0, 2, 0)
    val means: ParSeq[Point] = IndexedSeq(mean1, mean2).par
    val eta: Double = 12.25
    val exp1 = new Point(0.0, 0.0, 0.0)
    val exp2 = new Point(0.0, 5.5, 0.0)
    val expected: ParSeq[Point] = IndexedSeq(exp1, exp2).par
    //checkParKMeans(points, means, eta, expected)
    assert(kMeans(points, means, eta).containsSlice(expected),
      s"kMeans not correct.")
  }
*/

  import scala.concurrent.duration.*
  override val munitTimeout = 10.seconds


