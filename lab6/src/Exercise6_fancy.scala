import Concurrency.{log, thread}

import java.util.concurrent.*
import scala.util.Random

object mesa1 {
    //CS-smoker i: It cannot smoke until its missing ingredients are available
    //CS-agent: It cannot put new ingredients until the current smoker has finished to smoke

    private var ingr = -1 // The missing ingredient-- -1=empty table, 0= no tobacco, 1=no paper, 2=no matches

    private val mutex = new Semaphore(1, true) // for accessing the ingr
    private val agentSem = new Semaphore(1, true)
    private val smokerSem = new Semaphore(0, true)


    def wantsToSmoke(i: Int) = {
        // the smoker i wants to smoke
        smokerSem.acquire() // one single semaphore for all the smokers
        mutex.acquire()
        if (ingr == i) // if the ingredient is the right one, the smokers smokes: (does not release smokerSem)
            log(s"smoker $i smokes")
            // ends smoking:
            log(s"smoker $i ends smoking")
            agentSem.release()

        else // if the ingredient is not the needed one, the smoker sem is released
            smokerSem.release() // so another smokers can try to smoke
        mutex.release()

    }

    def endsSmoking(i: Int) = {
        // Not needed in this approach
    }

    def putAllIngredientsBut(ingr: Int) = {
        // the agent puts new ingredients (ingr is the non put ingredient)
        agentSem.acquire()

        mutex.acquire()
        this.ingr = ingr
        log(s"The agent does not put ingredient $ingr")
        mutex.release()

        smokerSem.release()


    }

}

object Exercise6_fancy {

    def main(args: Array[String]): Unit =
        val smoker = new Array[Thread](3)
        for (i <- smoker.indices)
            smoker(i) = thread {
                while (true) {
                    Thread.sleep(Random.nextInt(500))
                    mesa1.wantsToSmoke(i)
                    Thread.sleep(Random.nextInt(200))
                    mesa1.endsSmoking(i)
                }
            }
        val agent = thread {
            while (true) {
                Thread.sleep(Random.nextInt(500))
                mesa1.putAllIngredientsBut(Random.nextInt(3))
            }
        }
}
