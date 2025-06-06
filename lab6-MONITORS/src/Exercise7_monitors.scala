import lab6.{log, thread}

import java.util.concurrent.*
import scala.util.Random

class Nest(B: Int) {
    //CS-baby i: cannot take a bug if the availableBugs is empty
    //CS-father/mother: cannot put a bug if the availableBugs is full

    private var availableBugs = 0


    def takeBug(i: Int) = synchronized {
        // Baby i takes a bug from the availableBugs
        while (availableBugs == 0)
            wait()

        availableBugs -= 1
        notifyAll() // notify the parents

        log(s"Baby $i takes a bug. Remaining $availableBugs bugs")

    }

    def putBug(i: Int) = synchronized {
        // The father/mother puts a bug on the availableBugs (0=father, 1=mother)
        while (availableBugs == B)
            wait()

        availableBugs += 1
        notifyAll() // notify the chicks
        log(s"Father $i puts a bug. Remaining $availableBugs bugs")

    }
}

object Exercise7 {

    def main(args: Array[String]): Unit = {
        val N = 10
        val Nest = new Nest(5)
        val baby = new Array[Thread](N)
        for (i <- baby.indices)
            baby(i) = thread {
                while (true) {
                    Nest.takeBug(i)
                    Thread.sleep(Random.nextInt(600))
                }
            }
        val father = new Array[Thread](2)
        for (i <- father.indices) {
            father(i) = thread {
                while (true) {
                    Thread.sleep(Random.nextInt(100))
                    Nest.putBug(i)
                }
            }
        }
    }

}
