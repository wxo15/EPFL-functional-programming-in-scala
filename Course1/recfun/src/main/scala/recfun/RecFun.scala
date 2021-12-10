package recfun

import javax.net.ssl.TrustManager

object RecFun extends RecFunInterface:

  def main(args: Array[String]): Unit =
    println("Pascal's Triangle")
    for row <- 0 to 10 do
      for col <- 0 to row do
        print(s"${pascal(col, row)} ")
      println()

  /**
   * Exercise 1
   */
  def pascal(c: Int, r: Int): Int = {
    if (c==0 || c==r) 1
    else {
      pascal(c-1,r-1) + pascal(c, r-1)
    }
  }

  /**
   * Exercise 2
   */
  def balance(chars: List[Char]): Boolean = {
    def netdepth(chars: List[Char], depth: Int): Int = {
      if (chars.isEmpty) depth
      else if (depth < 0) -1 // If depth < 0, stop recursing. It is False.
      else if (chars.head == '(') netdepth(chars.tail, depth + 1)
      else if (chars.head == ')') {netdepth(chars.tail, depth - 1)}
      else netdepth(chars.tail, depth)
    }

    netdepth(chars,0) == 0
  }

  /**
   * Exercise 3
   */
  def countChange(money: Int, coins: List[Int]): Int = {
    def count(money: Int, currcoin: Int, remainingcoins: List[Int]): Int = {
      if (money == 0) 1
      else if (money < 0) 0
      else if (remainingcoins.isEmpty) count(money - currcoin, currcoin, remainingcoins)
      else count(money - currcoin, currcoin, remainingcoins) + count(money, remainingcoins.head, remainingcoins.tail)
    }

    count(money, coins.sorted(Ordering.Int.reverse).head, coins.sorted(Ordering.Int.reverse).tail)
  }
