import Concurrency.parallel

import scala.util.Random

// returns true only the list (from start to end) contains TRUE ONLY
def allTrue(list: List[Boolean], start: Int, end: Int): Boolean =
    ! list.slice(start, end).contains(false)

// recursive version
def allTrueRec(list: List[Boolean], start: Int, end: Int): Boolean =
    if (end - start == 1)
        list(start)
    else
        allTrueRec(list, start, start + (end - start) / 2) && allTrueRec(list, start + (end - start) / 2, end)

// tail-recursive version
def allTrueTailRec(list: List[Boolean], start: Int, end: Int): Boolean =
    def loop(i: Int, acc: Boolean): Boolean =
        if (i >= end || !acc)
            acc
        else loop(i + 1, acc && list(i))

    loop(start, true)

object Exercise3 :
    def main(args: Array[String]) =
        val list = List.fill(Random.nextInt(12))(Random.nextBoolean())
        println(list)

        val (a, b) = parallel(
            allTrueRec(list, 0, list.length / 2), // midpoint is not included
            allTrueRec(list, list.length / 2, list.length)  // included here
        )

        println(a && b)


