import lab6.{log, thread}

import java.util.concurrent.*
import scala.util.Random

object mesa {
    //CS-smoker i: It cannot smoke until its missing ingredients are available
    //CS-agent: It cannot put new ingredients until the current smoker has finished to smoke

    private var ingr = -1 // The missing ingredient-- -1=empty table, 0= no tobacco, 1=no paper, 2=no matches

    def wantsToSmoke(i: Int) = synchronized {
        while (ingr != i)
            wait()
        // the smoker i wants to smoke
        log(s"smoker $i smokes")

    }

    def endsSmoking(i: Int) = synchronized {
        ingr = -1 // resets the table
        // The smoker i ends smoking
        log(s"smoker $i ends smoking")
        notifyAll() // notify the agent
    }

    def putAllIngredientsBut(ingr: Int) = synchronized {
        // log(s"putAllIngredients: $ingr")
        // the agent puts new ingredients (ingr is the non put ingredient)
        while (mesa.ingr != -1) // wait while the table is not empty
            //log(s"$ingr != -1")
            wait()

        // log(s"putAllIngredients: $ingr")
        mesa.ingr = ingr // set the missing ingredient
        notifyAll() // notify the smokers
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
