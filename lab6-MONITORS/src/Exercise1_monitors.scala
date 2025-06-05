import lab6.{log, thread}

import java.util.concurrent.*
import scala.util.Random

object sampling {
    // CS-Sensor-i:  sensor i cannot sample again until the worker has
    // finished to process previous samples
    // CS-Worker: it cannot start its tasks until the three samples
    // are available

    private var entryDoorSensor = true
    private var exitDoorSensor = false
    private var samplesReady = false
    private var numSamples = 0

    def newSample(id: Int) = synchronized {
        while (!entryDoorSensor)
            wait()

        numSamples += 1
        log(s"Sensor $id stores its sample")

        if (numSamples == 3) // if there are already 3 measures, close the entry door
            entryDoorSensor = false
            samplesReady = true
            notifyAll() // should be notifyAll cause worker waits in the same room as sensors to exit

        while (!exitDoorSensor) // waiting anyway
            wait()

        numSamples -= 1
        if (numSamples == 0) // the last sensor exiting resets the room
            exitDoorSensor = false
            entryDoorSensor = true
            notifyAll() // notify the sensors waiting to go in

    }

    def readSamples() = synchronized {
        while (!samplesReady)
            wait()

        log(s"Worker gathers the three samples")

    }

    def endWork() = synchronized {
        log(s"Worker has finished its tasks")

        samplesReady = false
        exitDoorSensor = true
        notifyAll() // notify the 3 sensors waiting for exiting
    }
}

object Exercise1 {

    def main(args: Array[String]) =
        val sensor = new Array[Thread](3)

        for (i <- 0 until sensor.length)
            sensor(i) = thread {
                while (true) {
                    Thread.sleep(Random.nextInt(100)) // measuring
                    sampling.newSample(i)
                }
            }

        val worker = thread {
            while (true) {
                sampling.readSamples()
                Thread.sleep(Random.nextInt(100)) // processing its task
                sampling.endWork()
            }
        }
}
