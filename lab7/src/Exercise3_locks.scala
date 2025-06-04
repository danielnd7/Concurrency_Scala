import scala.util.Random
import Concurrency.*

import java.util.concurrent.locks.ReentrantLock

object Couples_locks {
    @volatile private var doorMan = true
    @volatile private var doorWoman = true
    @volatile private var doorExit = false

    private val lock = new ReentrantLock(true)
    private val manCond = lock.newCondition() // waiting for the man door to open
    private val womanCond = lock.newCondition() // waiting for the woman door to open
    private val exitCond = lock.newCondition() // waiting for the EXIT door to open

    def manArrives(id: Int): Unit = {
        lock.lock()
        try
            while (!doorMan)
                manCond.await() // wait in a queue for men

            doorMan = false

            log(s"Man $id wants a girlfriend")

            if (!doorWoman) // woman is inside
                log("A couple has been formed!!!")
                doorExit = true
                exitCond.signal() // signal to woman

            else // there is no woman inside
                while !doorExit do exitCond.await() // wait in exit queue

                doorMan = true
                doorWoman = true
                doorExit = false

                manCond.signal() // wake up one man
                womanCond.signal() // wake up one woman

        finally
            lock.unlock()
    }

    def womanArrives(id: Int): Unit = {
        lock.lock()
        try
            while (!doorWoman)
                womanCond.await()

            doorWoman = false

            log(s"Woman $id wants a boyfriend")

            if (!doorMan) // man is inside
                doorExit = true
                log("A couple has been formed!!!")
                exitCond.signal()

            else // there is no man inside
                while !doorExit do exitCond.await() // wait in exit queue

                doorMan = true
                doorWoman = true
                doorExit = false

                manCond.signal()
                womanCond.signal()


        finally
            lock.unlock()
    }

}

object Exercise3_locks {

    def main(args: Array[String]): Unit = {
        val numPairs = 10
        val women = new Array[Thread](numPairs)
        val men = new Array[Thread](numPairs)

        for (i <- men.indices)
            men(i) = thread {
                Couples_locks.manArrives(i)
            }

        for (i <- women.indices)
            women(i) = thread {
                Couples_locks.womanArrives(i)
            }


    }

}
