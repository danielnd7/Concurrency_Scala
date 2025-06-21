import Concurrency.thread
import java.util.concurrent.Semaphore
import scala.util.Random

object Cafeteria :
  // Synchronization conditions:
  // SC-1: A student cannot put a tray if there is no empty station.
  // SC-2: The worker cannot take a tray if there is no one available.

  private val NUM_STATIONS = 4

  def takeTray =
  
    // Worker flushed the content
    println(s"Worker has taken from a tray")
    Thread.sleep(Random.nextInt(300))


  def putTray(id: Int) =
  
    println(s"Student $id puts in a tray")


object MainCafeteria :
  private val NUM_STUDENTS = 12
  def main(args: Array[String]) =
    val students = new Array[Thread](NUM_STUDENTS)
    for (i <- students.indices)
      students(i) = thread {
        // Student eats
        Thread.sleep(Random.nextInt(2000))
        Cafeteria.putTray(i)
      }
    val worker = thread {
        for(i <- 1 to NUM_STUDENTS)
          Cafeteria.takeTray
          // Worker rests a bit
          Thread.sleep(Random.nextInt(200))
      }
    for (i <- students.indices)
      students(i).join
    worker.join
    println("End Of Program")