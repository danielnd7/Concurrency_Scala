import lab6.{log, thread}

import java.util.concurrent.*
import java.util.concurrent.locks.ReentrantLock
import scala.util.Random

object sampling_locks {
    // CS-Sensor-i:  sensor i cannot sample again until the worker has
    // finished to process previous samples
    // CS-Worker: it cannot start its tasks until the three samples
    // are available

    @volatile private var entryDoorSensor = true
    @volatile private var exitDoorSensor = false
    @volatile private var samplesReady = false
    @volatile private var numSamples = 0
    
    private val lock = new ReentrantLock(true)
    private val roomExitSensors = lock.newCondition()
    private val roomEnterSensors = lock.newCondition()
    private val roomWorker = lock.newCondition()

    def newSample(id: Int) = {
        lock.lock()
        try 
                
            while (!entryDoorSensor)
                roomEnterSensors.await()
    
            numSamples += 1
            log(s"Sensor $id stores its sample")
    
            if (numSamples == 3) // if there are already 3 measures, close the entry door
                entryDoorSensor = false
                samplesReady = true
                roomWorker.signal() // signal to worker
    
            while (!exitDoorSensor) // waiting anyway
                roomExitSensors.await()
    
            numSamples -= 1
            if (numSamples == 0) // the last sensor exiting resets the room
                exitDoorSensor = false
                entryDoorSensor = true
                roomEnterSensors.signalAll() // notify the sensors waiting to go in

        finally
            lock.unlock()

    }

    def readSamples() = {
        lock.lock()
        try
                
            while (!samplesReady)
                roomWorker.await()

            log(s"Worker gathers the three samples")

        finally
            lock.unlock()

    }

    def endWork() = {
        lock.lock()
        try
            log(s"Worker has finished its tasks")

            samplesReady = false
            exitDoorSensor = true
            roomExitSensors.signalAll() // notify the 3 sensors waiting for exiting

        finally
            lock.unlock()
    }
}

object Exercise1_locks {

    def main(args: Array[String]) =
        val sensor = new Array[Thread](3)

        for (i <- 0 until sensor.length)
            sensor(i) = thread {
                while (true) {
                    Thread.sleep(Random.nextInt(100)) // measuring
                    sampling_locks.newSample(i)
                }
            }

        val worker = thread {
            while (true) {
                sampling_locks.readSamples()
                Thread.sleep(Random.nextInt(100)) // processing its task
                sampling_locks.endWork()
            }
        }
}
