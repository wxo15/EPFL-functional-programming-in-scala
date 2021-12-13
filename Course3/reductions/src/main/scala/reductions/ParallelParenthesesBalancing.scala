package reductions

import scala.annotation.*
import org.scalameter.*

object ParallelParenthesesBalancingRunner:

  @volatile var seqResult = false

  @volatile var parResult = false

  val standardConfig = config(
    Key.exec.minWarmupRuns := 40,
    Key.exec.maxWarmupRuns := 80,
    Key.exec.benchRuns := 120,
    Key.verbose := false
  ) withWarmer(Warmer.Default())

  def main(args: Array[String]): Unit =
    val length = 100000000
    val chars = new Array[Char](length)
    val threshold = 10000
    val seqtime = standardConfig measure {
      seqResult = ParallelParenthesesBalancing.balance(chars)
    }
    println(s"sequential result = $seqResult")
    println(s"sequential balancing time: $seqtime")

    val fjtime = standardConfig measure {
      parResult = ParallelParenthesesBalancing.parBalance(chars, threshold)
    }
    println(s"parallel result = $parResult")
    println(s"parallel balancing time: $fjtime")
    println(s"speedup: ${seqtime.value / fjtime.value}")

object ParallelParenthesesBalancing extends ParallelParenthesesBalancingInterface:

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
   */
  def balance(chars: Array[Char]): Boolean =
    def netdepth(idx: Int, until: Int, depth: Int, mindepth: Int) : (Int, Int) = {
      var newmindepth = scala.math.min(mindepth, depth)
      if idx > until then (depth, newmindepth)
      else if (chars(idx) == '(') netdepth(idx + 1, until, depth + 1, newmindepth)
      else if (chars(idx) == ')') netdepth(idx + 1, until, depth - 1, newmindepth)
      else netdepth(idx + 1, until, depth, newmindepth)
    }
    if chars.isEmpty then true
    else netdepth(0, chars.length - 1, 0, 0) == (0, 0)

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
   */
  def parBalance(chars: Array[Char], threshold: Int): Boolean =

    def traverse(idx: Int, until: Int, depth: Int, mindepth: Int) : (Int, Int) = {
      var newmindepth = scala.math.min(mindepth, depth)
      if idx > until then (depth, newmindepth)
      else if (chars(idx) == '(') traverse(idx + 1, until, depth + 1, newmindepth)
      else if (chars(idx) == ')') traverse(idx + 1, until, depth - 1, newmindepth)
      else traverse(idx + 1, until, depth, newmindepth)
    }

    def reduce(from: Int, until: Int) : (Int, Int) = { // Final depth, min depth
      if until - from <= threshold then traverse(from, until, 0, 0)
      else {
        val middle = (until + from) / 2
        val (left, right) = parallel(reduce(from, middle),reduce(middle, until))
        (left._1 + right._1, scala.math.min(left._2, left._1 + right._2))
      }
    }
    if chars.isEmpty then true
    else reduce(0, chars.length-1) == (0, 0)

  // For those who want more:
  // Prove that your reduction operator is associative!

