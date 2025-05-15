import Concurrency._
import java.util.concurrent.Semaphore

object count :
    private var n: Int = 0
    private val mutex = new Semaphore(1, true)

    def inc() =
        mutex.acquire()
        n += 1
        mutex.release()

    def num() = n


object MutualExcusion extends App :
    val t1 = thread {
        for (i <- 0 until 100)
            count.inc()
    }
    val t2 = thread {
        for (i <- 0 until 100)
            count.inc()
    }
    t1.join()
    t2.join()

    println(s"Total number n = ${count.num()}")

