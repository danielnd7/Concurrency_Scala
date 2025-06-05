import scala.util.Random
import Concurrency.*

import java.util.concurrent.locks.ReentrantLock

class Tray_lock(portionsPerCake: Int):

    @volatile private var numAvailPortions = 0
    // @volatile private var hasPortions = false // NOT NEEDED
    // @volatile private var isEmpty = true // NOT NEEDED

    private val lock = new ReentrantLock(true)
    private val roomBaker = lock.newCondition()
    private val roomChildren = lock.newCondition()


    def wantPortion(id: Int): Unit = {
        lock.lock()
        try
            while (numAvailPortions == 0) roomChildren.await()

            numAvailPortions -= 1
            log(s"Child $id took a portion. Remaining: $numAvailPortions")
            if (numAvailPortions == 0)
                roomBaker.signal()

        finally
            lock.unlock()
    }

    def bakeCake(): Unit = {
        lock.lock()
        try
            while (numAvailPortions > 0) roomBaker.await()

            numAvailPortions = portionsPerCake
            log("The baker places a new cake with fresh numAvailPortions.")

            roomChildren.signalAll()

        finally
            lock.unlock()
    }

object Exercise6_locks:

    def main(args: Array[String]): Unit =
        val portionsPerCake = 5
        val numChildren = 10

        val tray = new Tray_lock(portionsPerCake)
        val children = Array.tabulate(numChildren)(i =>
            thread:
                while true do
                    Thread.sleep(Random.nextInt(500))
                    tray.wantPortion(i)
        )

        val baker = thread:
            while true do
                Thread.sleep(Random.nextInt(100))
                tray.bakeCake()

