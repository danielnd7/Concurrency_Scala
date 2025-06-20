import Concurrency.{log, thread}

import java.util.concurrent.*
import scala.util.Random

class Belt(n: Int) {
    // CS-Packager-i: waits for products of code i
    // CS-Robot: waits if there are n products in the belt (it is full)
    private val buffer = Array.fill(3)(0)
    private var numItems = 0
    private val isFreeSpace = new Semaphore(1, true)
    private val packagersSem = new Array[Semaphore](3)
    packagersSem.indices.foreach(packagersSem(_) = new Semaphore(0, true)) // 0 - the cell is empty, 1 - not empty
    private val mutex = new Semaphore(1, true)

    def takeProduct(p: Int) = {
        packagersSem(p).acquire()

        mutex.acquire()

        numItems -= 1
        buffer(p) -= 1
        log(s"Packager $p takes a product. Remaining ${buffer.mkString("[", ",", "]")}")

        if (buffer(p) > 0) // if there is more packages to pack
            packagersSem(p).release() // the corresponding semaphore is released

        if (numItems == n - 1) // if after taking an item the conveyor is not full anymore
            isFreeSpace.release() // wake up the main robot

        mutex.release()
    }

    def newProduct(p: Int) = {
        isFreeSpace.acquire()

        mutex.acquire()

        numItems += 1
        buffer(p) += 1

        log(s"Robot puts a product $p. Remaining ${buffer.mkString("[", ",", "]")}")
        log(s"Total of packaged products $numItems")

        if (buffer(p) == 1) // if the first item for a packager
            packagersSem(p).release() // wake up the packager

        if (numItems < n)
            isFreeSpace.release()

        mutex.release()
    }
}

object Exercise2 {
    def main(args: Array[String]) = {
        val belt = new Belt(6)
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
