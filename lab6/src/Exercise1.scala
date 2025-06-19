import Concurrency.{log, thread}

import java.util.concurrent.*
import scala.util.Random
object sampling{
    // CS-Sensor-i:  sensor i cannot sample again until the worker has
    // finished to process previous samples
    // CS-Worker: it cannot start its tasks until the three samples
    // are available

    private val samplesReady = new Semaphore(0, true)
    private val sampling = new Semaphore(1, true)
    private val mutex = new Semaphore(1, true)
    private var numSamples = 0
    private val alreadySampled = new Array[Boolean](3)



    def newSample(id:Int) = {
        sampling.acquire()
        mutex.acquire()

        if (!alreadySampled(id))

            numSamples += 1
            alreadySampled(id) = true

            log(s"Sensor $id stores its sample" )
            if (numSamples == 3)
                samplesReady.release()

        mutex.release()
        sampling.release()
    }

    def readSamples() = {
        samplesReady.acquire()
        sampling.acquire()

        mutex.acquire()
        numSamples = 0
        alreadySampled.indices.foreach(alreadySampled(_) = false)
        mutex.release()

        log(s"Worker gathers the three samples")

    }

    def endWork()={
        sampling.release()
        log(s"Worker has finished its tasks")
    }
}

object Exercise1 {

    def main(args:Array[String]) =
        val sensor=new Array[Thread](3)

        for (i<-0 until sensor.length)
            sensor(i) = thread {
                while (true) {
                    Thread.sleep(Random.nextInt(100)) // measuring
                    sampling.newSample(i)
                }
            }

        val worker = thread {
            while (true){
                sampling.readSamples()
                Thread.sleep(Random.nextInt(100)) // processing its task
                sampling.endWork()
            }
        }
}
