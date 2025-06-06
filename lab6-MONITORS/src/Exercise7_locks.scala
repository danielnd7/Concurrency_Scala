import lab6.{log, thread}

import java.util.concurrent.*
import java.util.concurrent.locks.ReentrantLock
import scala.util.Random

class Nest_locks(B: Int) {
    //CS-baby i: cannot take a bug if the availableBugs is empty
    //CS-father/mother: cannot put a bug if the availableBugs is full

    @volatile private var availableBugs = 0

    private val lock = new ReentrantLock(true)
    private val roomChicks = lock.newCondition()
    private val roomParents = lock.newCondition()

    def takeBug(i: Int) = {
        lock.lock()
        try

            // Baby i takes a bug from the availableBugs
            while (availableBugs == 0)
                roomChicks.await()

            availableBugs -= 1
            roomParents.signalAll() // notify the parents

            log(s"# Baby $i takes a bug. Remaining $availableBugs bugs")

        finally
            lock.unlock()

    }

    def putBug(i: Int) = {
        lock.lock()
        try

            // The father/mother puts a bug on the availableBugs (0=father, 1=mother)
            while (availableBugs == B)
                roomParents.await()

            availableBugs += 1
            roomChicks.signalAll() // notify the chicks

            log(s"# Father $i puts a bug. Remaining $availableBugs bugs")

        finally
            lock.unlock()
    }
}

object Exercise7_locks {

    def main(args: Array[String]): Unit = {
        val N = 10
        val nest = new Nest_locks(5)
        val baby = new Array[Thread](N)
        for (i <- baby.indices)
            baby(i) = thread {
                while (true) {
                    nest.takeBug(i)
                    Thread.sleep(Random.nextInt(600))
                }
            }
        val father = new Array[Thread](2)
        for (i <- father.indices) {
            father(i) = thread {
                while (true) {
                    Thread.sleep(Random.nextInt(100))
                    nest.putBug(i)
                }
            }
        }
    }

}
