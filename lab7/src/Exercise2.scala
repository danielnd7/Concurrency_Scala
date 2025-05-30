import scala.util.Random
import Concurrency._
import scala.collection.mutable.ListBuffer

class Resources(total: Int) {

    private val waitingQueue = new ListBuffer[Int]()      // Queue of waiting process IDs
    private var available = total                         // Number of available resources
    //private var next = -1                                 // Next process ID to be served
    //private var waitingCount = 0                          // Number of processes currently waiting

    def requestResources(id: Int, amount: Int): Unit = synchronized {
        // Process `id` requests `amount` resources
        waitingQueue += (id)
        log(s"Process $id requests $amount resources. Waiting count: ${waitingQueue.length}, waiting queue: ${waitingQueue}")

        // the following part can be implemented with a single while(... || ... )
        while (waitingQueue(0) != id) // not first in queue
            wait()

        while (amount > available)
            wait()

        // ACQUIRING
        available -= amount
        waitingQueue.remove(0) // exit from the queue
        notifyAll() // in case the next process can wants to acquire resources
        log(s"Process $id acquires $amount resources. Remaining: $available, waiting queue: ${waitingQueue}")
    }

    def releaseResources(id: Int, amount: Int): Unit = synchronized {
        // Process `id` releases `amount` resources
        available += amount
        notifyAll()
        log(s"Process $id releases $amount resources. Available now: $available")
    }


}

object Exercise2 {

    def main(args: Array[String]): Unit = {
        val totalResources = 5
        val numProcesses = 10

        val resources = new Resources(totalResources)
        val processes = new Array[Thread](numProcesses)

        for (i <- processes.indices)
            processes(i) = thread {
                val r = Random.nextInt(totalResources) + 1
                resources.requestResources(i, r)
                Thread.sleep(Random.nextInt(300))
                resources.releaseResources(i, r)
            }
    }

}
