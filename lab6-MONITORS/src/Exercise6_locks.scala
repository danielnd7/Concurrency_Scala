import lab6.{log, thread}

import java.util.concurrent.*
import java.util.concurrent.locks.{Condition, ReentrantLock}
import scala.util.Random

object mesa_locks {
    //CS-smoker i: It cannot smoke until its missing ingredients are available
    //CS-agent: It cannot put new ingredients until the current smoker has finished to smoke

    @volatile private var ingr = -1 // The missing ingredient-- -1=empty table, 0= no tobacco, 1=no paper, 2=no matches
    private val lock = new ReentrantLock(true)
    private val roomAgent = lock.newCondition()
    private val roomsSmokers = new Array[Condition](3)
    roomsSmokers.indices.foreach(roomsSmokers(_) = lock.newCondition())
    def wantsToSmoke(i: Int) = {
        lock.lock()
        try

            while (ingr != i)
                roomsSmokers(i).await()

            // the smoker i wants to smoke
            log(s"# smoker $i smokes")

        finally
            lock.unlock()

    }

    def endsSmoking(i: Int) = {
        lock.lock()
        try

            ingr = -1 // resets the table
            // The smoker i ends smoking
            log(s"# smoker $i ends smoking")
            roomAgent.signal() // notify the agent

        finally
            lock.unlock()
    }

    def putAllIngredientsBut(ingr: Int) = {
        lock.lock()
        try

            // the agent puts new ingredients (ingr is the non put ingredient)
            while (mesa_locks.ingr != -1) // wait while the table is not empty
                roomAgent.await()


            mesa_locks.ingr = ingr // set the missing ingredient
            roomsSmokers(ingr).signal() // notify the smoker

            log(s"# The agent does not put ingredient $ingr")

        finally
            lock.unlock()

    }

}

object Exercise6_locks {

    def main(args: Array[String]): Unit =
        val smoker = new Array[Thread](3)
        for (i <- smoker.indices)
            smoker(i) = thread {
                while (true) {
                    Thread.sleep(Random.nextInt(500))
                    mesa_locks.wantsToSmoke(i)
                    Thread.sleep(Random.nextInt(200))
                    mesa_locks.endsSmoking(i)
                }
            }
        val agent = thread {
            while (true) {
                Thread.sleep(Random.nextInt(500))
                mesa_locks.putAllIngredientsBut(Random.nextInt(3))
            }
        }
}
