import scala.util.Random
import Concurrency._

class Buffer(numConsumers: Int, size: Int) {
    // numConsumers - number of consumers
    // size - buffer size

    private val buffer = new Array[Int](size)
    buffer.indices.foreach(buffer(_) = 0) // to be displayed


    private val bufferTimesConsumed = new Array[Int](size)
    bufferTimesConsumed.indices.foreach(bufferTimesConsumed(_) = 0)

    // if buffer(i) holds a data item, bufferTimesConsumed(i) holds the number of consumers
    // who already consumed the data

    // Variables for Producer
    private var freeSlots = size
    private var producerIndex = 0

    // Variables for consumers
    private val availableItems = new Array[Int](numConsumers)
    availableItems.indices.foreach(availableItems(_) = 0) // items each consumer have consumed

    private val consumerIndices = new Array[Int](numConsumers) // indices to consume
    consumerIndices.indices.foreach(consumerIndices(_) = 0) // index for each consumer

    def newData(item: Int): Unit = synchronized {
        // producer inserts a new item
        while (freeSlots == 0)
            wait()

        buffer(producerIndex) = item// simulates data stored
        log(s"      Producer stores $item: buffer=${buffer.mkString("[", ",", "]")}")

        freeSlots -= 1
        producerIndex = (producerIndex + 1) % size

        availableItems.indices.foreach(availableItems(_) += 1) // increase available items for all the consumers
        notifyAll()
    }

    def extractData(id: Int): Int = synchronized {
        while (availableItems(id) == 0)
            wait()

        val item = buffer(consumerIndices(id))

        bufferTimesConsumed(consumerIndices(id)) += 1
        availableItems(id) -= 1

        // if all consumers have consumed, this cell is freed in the buffer:
        if (bufferTimesConsumed( consumerIndices(id) ) == numConsumers)
            bufferTimesConsumed(consumerIndices(id)) = 0 // reset the bufferTimesConsumed
            buffer( consumerIndices(id) ) = 0
            freeSlots += 1

            notifyAll() // notify producer

        log(s"Consumer $id reads $item: buffer=${buffer.mkString("[", ",", "]")}")
        consumerIndices(id) = (consumerIndices(id) + 1) % size

        item
    }

}

object Exercise1 {

    def main(args: Array[String]): Unit = {
        val numConsumers = 4
        val bufferSize = 3
        val numIterations = 10

        val buffer = new Buffer(numConsumers, bufferSize)
        val consumers = new Array[Thread](numConsumers)

        for (i <- consumers.indices)
            consumers(i) = thread {
                for (_ <- 0 until numIterations) {
                    val item = buffer.extractData(i)
                    Thread.sleep(Random.nextInt(200))
                }
            }

        val producer = thread {
            for (i <- 0 until numIterations) {
                Thread.sleep(Random.nextInt(50))
                buffer.newData(i + 1)
            }
        }
    }

}
