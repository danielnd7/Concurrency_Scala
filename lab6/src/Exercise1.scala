import Concurrency.{log, thread}

import java.util.concurrent.*
import scala.util.Random
object sampling{
    // CS-Sensor-i:  sensor i cannot sample again until the worker has
    // finished to process previous samples
    // CS-Worker: it cannot start its tasks until the three samples
    // are available

    private val samplesReady = new Semaphore(0, true)
    private val mutex = new Semaphore(1, true)
    @volatile private var numSamples = 0

    private val sensorWait = new Array[Semaphore](3)
    sensorWait.indices.foreach(sensorWait(_) = new Semaphore(1, true))



    def newSample(id:Int) = {
        sensorWait(id).acquire()
        mutex.acquire()

        numSamples += 1
        log(s"Sensor $id stores its sample" )

        if (numSamples == 3)
            samplesReady.release()


        mutex.release()
    }

    def readSamples() = {
        samplesReady.acquire()
        log(s"Worker gathers the three samples")

    }

    def endWork()={
        mutex.acquire()

        numSamples = 0
        sensorWait.indices.foreach(sensorWait(_).release())
        log(s"Worker has finished its tasks")

        mutex.release()
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
