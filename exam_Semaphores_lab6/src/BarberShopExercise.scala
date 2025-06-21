import Concurrency.{log, thread}
import java.util.concurrent.Semaphore
import scala.util.Random

object BarberShop:
    // Synchronization conditions:
    // SC-1: A customer cannot enter in the Barbershop until the chair is empty
    // SC-2: A barber cannot start cutting the hair until the chair is in use.
    // SC-3: A customer cannot exit the BarberShop until the barber has finished to cut his/her hair.
    private val chair = new Semaphore(1, true) // 1 = the chair is empty, 0 = somebody is sitting on the chair
    private val exit = new Semaphore(0, true) // is released for the customer to go out
    private val customerWaiting = new Semaphore(0, true) // released by a customer, so the barbers are notified that there is a customer waiting

    def enterCustomer(id: Int) =
        chair.acquire()
        println(s"Customer $id is sat in the barber shop")

        customerWaiting.release()




    def exitCustomer(id: Int) =
        exit.acquire()
        chair.release()
        println(s"Customer $id exits the barber shop")

    def haircut(id: Int) =
        customerWaiting.acquire()
        println(s"Barber $id is cutting the hair")
        Thread.sleep(Random.nextInt(500))
        exit.release()


object MainBarberShop:
    private val NUM_BARBERS = 2
    private val NUM_CUSTOMERS = 10

    def main(args: Array[String]) =
        val barbers = new Array[Thread](NUM_BARBERS)
        val customers = new Array[Thread](NUM_CUSTOMERS)
        for (i <- barbers.indices)
            barbers(i) = thread {
                while (true)
                    BarberShop.haircut(i)
                    // Barber rests a bit
                    Thread.sleep(Random.nextInt(500))
            }
        for (i <- customers.indices)
            customers(i) = thread {
                while (true)
                    BarberShop.enterCustomer(i)
                    BarberShop.exitCustomer(i)
                    // Customer walks around
                    Thread.sleep(Random.nextInt(5000))
            }

