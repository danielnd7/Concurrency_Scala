import lab6.{log, thread}

import java.util.concurrent.*
import scala.util.Random

class Belt(n: Int) {
    // CS-Packager-i: waits for products of code i
    // CS-Robot: waits if there are n products in the belt (it is full)

    private var totalPackaged = 0
    private var sizeBuffer = 0 // number of elements in the buffer
    private val buffer = new Array[Int](3)
    buffer.indices.foreach(buffer(_) = 0) // to be displayed

    def takeProduct(p: Int) = synchronized {
        while (buffer(p) == 0)
            wait()

        buffer(p) -= 1
        sizeBuffer -= 1
        totalPackaged += 1
        notifyAll() // notify a producer robot

        log(s"Packager $p takes a product. Remaining ${buffer.mkString("[", ",", "]")}")
        log(s"Total of packaged products $totalPackaged")

    }

    def newProduct(p: Int) = synchronized {
        while (sizeBuffer == n)
            wait()

        buffer(p) += 1 // increase the number of products of type p (0, 1 or 2)
        sizeBuffer += 1
        notifyAll() // notify packagers
        log(s"Robot puts a product $p. Remaining ${buffer.mkString("[", ",", "]")}")

    }
}

object Exercise2_monitors {
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
