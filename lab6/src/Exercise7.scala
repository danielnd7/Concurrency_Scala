import Concurrency.{log, thread}

import java.util.concurrent.*
import scala.util.Random

class Nest(B: Int) {
    //CS-baby i: cannot take a bug if the plate is empty
    //CS-father/mother: cannot put a bug if the plate is full

    private var numBugs = 0

    private val chicksSem = new Semaphore(0, true) // 0 - the plate is empty, 1 - not empty
    private val parentsSem = new Semaphore(1, true) // 0 - the plate is full, 1 - not full
    private val mutex = new Semaphore(1, true)

    def takeBug(i: Int) = {
        // Baby i takes a bug from the plate
        chicksSem.acquire()
        mutex.acquire()
        numBugs -=1
        log(s"Baby $i takes a bug. Remaining $numBugs bugs")
        if (numBugs > 0) // there are still more bugs in the plate
            chicksSem.release() // another chicks is woken up

        if (numBugs == B - 1) // the plate is not full anymore
            parentsSem.release() // wake up parents waiting

        mutex.release()
    }

    def putBug(i: Int) = {
        // The father/mother puts a bug on the plate (0=father, 1=mother)

        parentsSem.acquire()
        mutex.acquire()
        numBugs += 1
        log(s"Father $i puts a bug. Remaining $numBugs bugs")
        if (numBugs < B) // if the plate is not full the fullSem is released so another parents can put new bugs
            parentsSem.release()

        if (numBugs == 1) // if this is the first bug in the plate
            chicksSem.release() // the waiting chick is woken up

        mutex.release()
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
