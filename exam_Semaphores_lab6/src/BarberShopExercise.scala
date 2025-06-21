import Concurrency.{log, thread}
import java.util.concurrent.Semaphore
import scala.util.Random

object BarberShop :
  // Synchronization conditions:
  // SC-1: A customer cannot enter in the Barbershop until the chair is empty
  // SC-2: A barber cannot start cutting the hair until the chair is in use.
  // SC-3: A customer cannot exit the BarberShop until the barber has finished to cut his/her hair.



  def enterCustomer(id: Int) =
    println(s"Customer $id is sat in the barber shop")

  def exitCustomer(id: Int) =
    println(s"Customer $id exits the barber shop")

  def haircut(id: Int) =
    println(s"Barber $id is cutting the hair")
    Thread.sleep(Random.nextInt(500))

object MainBarberShop :
  private val NUM_BARBERS = 2
  private val NUM_CUSTOMERS = 10
  def main(args: Array[String]) =
    val barbers = new Array[Thread](NUM_BARBERS)
    val customers = new Array[Thread](NUM_CUSTOMERS)
    for (i <- barbers.indices)
      barbers(i) = thread {
        while(true)
          BarberShop.haircut(i)
          // Barber rests a bit
          Thread.sleep(Random.nextInt(500))
      }
    for (i <- customers.indices)
      customers(i) = thread {
        while(true)
          BarberShop.enterCustomer(i)
          BarberShop.exitCustomer(i)
          // Customer walks around
          Thread.sleep(Random.nextInt(5000))
      }

