import lab6.{log, thread}

import java.util.concurrent.*
import java.util.concurrent.locks.{Condition, ReentrantLock}
import scala.util.Random

class Belt_locks(n: Int) {
    // CS-Packager-i: waits for products of code i
    // CS-Robot: waits if there are n products in the belt (it is full)

    @volatile private var totalPackaged = 0
    @volatile private var sizeBuffer = 0 // number of elements in the buffer
    private val buffer = new Array[Int](3)
    buffer.indices.foreach(buffer(_) = 0) // to be displayed

    private val lock = new ReentrantLock(true)
    private val roomProducer = lock.newCondition()
    private val roomsPackagers = new Array[Condition](3) // rooms for packager robots
    roomsPackagers.indices.foreach(roomsPackagers(_) = lock.newCondition()) // fill the array

    def takeProduct(p: Int) = {
        lock.lock()
        try

            while (buffer(p) == 0)
                roomsPackagers(p).await()

            buffer(p) -= 1
            sizeBuffer -= 1
            totalPackaged += 1
            roomProducer.signal() // notify a producer robot

            log(s"# Packager $p takes a product. Remaining ${buffer.mkString("[", ",", "]")}")
            log(s"# Total of packaged products $totalPackaged")

        finally
            lock.unlock()

    }

    def newProduct(p: Int) = {
        lock.lock()
        try

            while (sizeBuffer == n)
                roomProducer.await()

            buffer(p) += 1 // increase the number of products of type p (0, 1 or 2)
            sizeBuffer += 1
            roomsPackagers(p).signal() // notify a specific packagers

            log(s"# Robot puts a product $p. Remaining ${buffer.mkString("[", ",", "]")}")

        finally
            lock.unlock()

    }
}

object Exercise2_locks {
    def main(args: Array[String]) = {
        val belt = new Belt_locks(6)
        val empaquetador = new Array[Thread](3)
        for (i <- 0 until empaquetador.length)
            empaquetador(i) = thread {
                while (true) {
                    belt.takeProduct(i)
                    Thread.sleep(Random.nextInt(500)) //empaquetando
                }
            }

        val robot = thread {
            while (true) {
                Thread.sleep(Random.nextInt(100)) //recogiendo el producto
                belt.newProduct(Random.nextInt(3))
            }
        }
    }

}
