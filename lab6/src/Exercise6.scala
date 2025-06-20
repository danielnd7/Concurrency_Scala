import Concurrency.{log, thread}

import java.util.concurrent.*
import scala.util.Random

object mesa {
    //CS-smoker i: It cannot smoke until its missing ingredients are available
    //CS-agent: It cannot put new ingredients until the current smoker has finished to smoke

    private var ingr = -1 // The missing ingredient-- -1=empty table, 0= no tobacco, 1=no paper, 2=no matches

    private val agentSem = new Semaphore(1, true)
    private val smokersSem = new Array[Semaphore](3)
    smokersSem.indices.foreach(smokersSem(_) = new Semaphore(0, true))

    def wantsToSmoke(i: Int) = {
        // the smoker i wants to smoke
        smokersSem(i).acquire()
        log(s"smoker $i smokes")
    }

    def endsSmoking(i: Int) = {
        // The smoker i ends smoking
        log(s"smoker $i ends smoking")
        agentSem.release()
    }

    def putAllIngredientsBut(ingr: Int) = {
        // the agent puts new ingredients (ingr is the non put ingredient)
        agentSem.acquire()
        smokersSem(ingr).release()
        log(s"The agent does not put ingredient $ingr")
    }

}

object Exercise6 {

    def main(args: Array[String]): Unit =
        val smoker = new Array[Thread](3)
        for (i <- smoker.indices)
            smoker(i) = thread {
                while (true) {
                    Thread.sleep(Random.nextInt(500))
                    mesa.wantsToSmoke(i)
                    Thread.sleep(Random.nextInt(200))
                    mesa.endsSmoking(i)
                }
            }
        val agent = thread {
            while (true) {
                Thread.sleep(Random.nextInt(500))
                mesa.putAllIngredientsBut(Random.nextInt(3))
            }
        }
}
