import scala.util.Random
import Concurrency._

class Tray(portionsPerCake: Int):

    private var numAvailPortions = 0
    private var hasPortions = false // NOT NEEDED
    private var isEmpty = true // NOT NEEDED

    def wantPortion(id: Int): Unit = synchronized {
        while (numAvailPortions == 0) wait()

        numAvailPortions -= 1
        log(s"Child $id took a portion. Remaining: $numAvailPortions")
        if (numAvailPortions == 0)
            notify()

    }

    def bakeCake(): Unit = synchronized {
        while (numAvailPortions > 0) wait()
        numAvailPortions = portionsPerCake
        log("The baker places a new cake with fresh numAvailPortions.")
        notifyAll()
    }

object Exercise6:

    def main(args: Array[String]): Unit =
        val portionsPerCake = 5
        val numChildren = 10

        val tray = new Tray(portionsPerCake)
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

