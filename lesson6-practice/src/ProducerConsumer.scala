import Concurrency.{log, thread}

import java.util.concurrent.Semaphore
import scala.util.Random
class Buffer(n: Int) :
    private val array = new Array[Int](n)
    private val isThereSpace = new Semaphore(n)
    private val areThereData = new Semaphore(0)
    private val mutex = new Semaphore(1)

    private var indexProd = 0
    private var indexCons = 0

    def take(): Int = {
        areThereData.acquire() // the order is crucial for avoiding dead lock
        mutex.acquire() // the order is crucial for avoiding dead lock

        val aux = array(indexCons)
        indexCons = (indexCons + 1) % n

        mutex.release()
        isThereSpace.release()
        aux
    }

    def store(num: Int) = {
        isThereSpace.acquire() // the order is crucial for avoiding dead lock
        mutex.acquire() // the order is crucial for avoiding dead lock

        array(indexProd) = num
        indexProd = (indexProd + 1) % n

        mutex.release()
        areThereData.release()
    }


object ProducerConsumer :
    def main() =
        val buffer = new Buffer(5)
        val prod = thread {
            for (i <- 0 until 20) {
                Thread.sleep(Random.nextInt(50))

                buffer.store(i)
                log(s"stored: ${i}")
            }
        }
        val cons = thread {
            for (i <- 0 until 20) {
                log(s"consumed : ${buffer.take()}")
                Thread.sleep(Random.nextInt(260))
            }
        }
        cons.join()
        prod.join()
