import Concurrency.thread
import java.util.concurrent.Semaphore
import scala.util.Random

object Cafeteria :
  // Synchronization conditions:
  // SC-1: A student cannot put a tray if there is no empty station.
  // SC-2: The worker cannot take a tray if there is no one available.

  private val NUM_STATIONS = 4
  private var num_trays = 0
  private val mutex = new Semaphore(1, true)
  private val stationNotFull = new Semaphore(1, true) // 1 = station is not full, 0 = is full
  private val stationNotEmpty = new Semaphore(0, true)

  def takeTray =
    stationNotEmpty.acquire()
    mutex.acquire()
    num_trays -= 1
    // Worker flushed the content
    println(s"Worker has taken from a tray")
    if (num_trays == NUM_STATIONS - 1)
      stationNotFull.release()
      
    if (num_trays > 0)
      stationNotEmpty.release()   
      
    mutex.release()
    Thread.sleep(Random.nextInt(300))
    


  def putTray(id: Int) =
    stationNotFull.acquire()
    mutex.acquire()

    num_trays += 1
    println(s"Student $id puts in a tray")
    if (num_trays < NUM_STATIONS)
      stationNotFull.release()
      
    if (num_trays == 1)
      stationNotEmpty.release()

    mutex.release()



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