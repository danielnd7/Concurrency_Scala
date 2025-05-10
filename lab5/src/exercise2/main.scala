package exercise2

import Concurrency.*
import exercise1.Peterson

import scala.util.Random


object Lake :
    val ptrsnIn = new Peterson
    val ptrsnOut = new Peterson
    val ptrsnMix = new Peterson // 0 for input. 1 for output
    @volatile var level = 0
    def addWater0(): Unit =
        ptrsnIn.enter0()
        ptrsnMix.enter0()

        level += 1 // CS
        log("level++ : " + level)

        ptrsnMix.exit0()
        ptrsnIn.exit0()
    def addWater1(): Unit =
        ptrsnIn.enter1()
        ptrsnMix.enter0()

        level += 1 // CS
        log("level++ : " + level)


        ptrsnMix.exit0()
        ptrsnIn.exit1()

    def takeWater0(): Unit =
        ptrsnOut.enter0()
        while (level == 0) Thread.sleep(0)
        ptrsnMix.enter1()

        level -= 1
        log("level-- : " + level)

        ptrsnMix.exit1()
        ptrsnOut.exit0()


    def takeWater1(): Unit =
        ptrsnOut.enter1()
        while (level == 0) Thread.sleep(0)
        ptrsnMix.enter1()

        level -= 1
        log("level-- : " + level)

        ptrsnMix.exit1()
        ptrsnOut.exit1()


@main
def main(): Unit =
    val N = 5
    val river0 = thread {
        for (i <- 0 until N)
            Thread.sleep(Random.nextInt(100))
            Lake.addWater0()
    }
    val river1 = thread {
        for (i <- 0 until N)
            Thread.sleep(Random.nextInt(100))
            Lake.addWater1()
    }
    val dam0 = thread {
        for (i <- 0 until N)
            Thread.sleep(Random.nextInt(100))
            Lake.takeWater0()
    }
    val dam1 = thread {
        for (i <- 0 until N)
            Thread.sleep(Random.nextInt(100))
            Lake.takeWater1()
    }
    river1.join()
    river0.join()
    dam0.join()
    dam1.join()

    println("end of the program: " + Lake.level)




