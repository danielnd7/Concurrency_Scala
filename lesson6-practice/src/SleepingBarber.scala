import Concurrency.{log, thread}

import java.util.concurrent.Semaphore
import scala.util.Random

class BarberShop :
    private var n = 0
    private val wait = new Semaphore(0, true)
    private val mutex = new Semaphore(1, true)

    def newClient() =
        mutex.acquire()
        n += 1
        log(s"A client has arrived $n")
        if (n == 0) wait.release()
        mutex.release()

    def shave() =
        mutex.acquire()
        n -= 1
        if (n == -1) {
            mutex.release()
            wait.acquire()
            mutex.acquire()
        }
        Thread.sleep(Random.nextInt(500))
        log(s"I've shaved a client $n")
        mutex.release()

object SleepingBarber {
    def main() =
        val barberShop = new BarberShop
        val barber = thread{
            while (true){
                barberShop.shave()
            }
        }
        val environment = thread{
            while (true){
                barberShop.newClient()
                //Thread.sleep(Random.nextInt(100))
            }
        }
}