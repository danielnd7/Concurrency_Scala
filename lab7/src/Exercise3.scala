import scala.util.Random
import Concurrency._

object Couples {
//    private var count = 0
    private var doorMan = true
    private var doorWoman = true
    private var doorExit = false

    def manArrives(id: Int): Unit = synchronized {
        while (!doorMan)
            wait()
        doorMan = false

        log(s"Man $id wants a girlfriend")

        if (!doorWoman) // woman is inside
            log("A couple has been formed!!!")
            doorExit = true
            notifyAll()
            
        else // there is no woman inside
            while !doorExit do wait() // wait for her

            doorMan = true
            doorWoman = true
            doorExit = false
            notifyAll()
    }

    def womanArrives(id: Int): Unit = synchronized {
        while (!doorWoman)
            wait()
        doorWoman = false

        log(s"Woman $id wants a boyfriend")

        if (!doorMan) // man is inside
            doorExit = true
            log("A couple has been formed!!!")
            notifyAll()
            
        else // there is no man inside
            while !doorExit do wait() // wait for him

            doorMan = true
            doorWoman = true
            doorExit = false
            notifyAll()
    }

}

object Exercise3 {

    def main(args: Array[String]): Unit = {
        val numPairs = 10
        val women = new Array[Thread](numPairs)
        val men = new Array[Thread](numPairs)

        for (i <- women.indices)
            women(i) = thread {
                Couples.womanArrives(i)
            }

        for (i <- men.indices)
            men(i) = thread {
                Couples.manArrives(i)
            }
    }

}
