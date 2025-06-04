import scala.util.Random
import Concurrency.*

import java.util.concurrent.locks.ReentrantLock

class Car_locks(capacity: Int) extends Thread {
    // CS-passenger1: if the car is full, a passenger cannot board until the trip is over
    // and the current passengers have gotten off
    // CS-passenger2: a passenger in the car cannot get off until the ride ends
    // CS-car: the car waits until C passengers have boarded before starting a ride

    @volatile private var numPassengers = 0
    @volatile private var entryDoor = true
    @volatile private var exitDoor = false
    @volatile private var readyForTrip = false
    
    private val lock = new ReentrantLock(true)
    private val roomToEnter = lock.newCondition()
    private val roomToExit = lock.newCondition()
    private val roomToStartTrip = lock.newCondition()

    def takeRide(id: Int): Unit = {
        lock.lock()
        try 
            // passenger id wants to take a ride on the roller coaster
            while (!entryDoor)
                roomToEnter.await()
    
            numPassengers += 1
            log(s"Passenger $id boards the car. Total: $numPassengers passengers.")
    
            if (numPassengers == capacity)
                entryDoor = false
                readyForTrip = true
                roomToStartTrip.signal() // signal a car driver
    
            while (!exitDoor)
                roomToExit.await()
    
            numPassengers -= 1
            log(s"Passenger $id exits the car. Remaining: $numPassengers passengers.")
    
            if (numPassengers == 0)
                exitDoor = false
                entryDoor = true
                roomToEnter.signalAll() // signal everybody waiting for the trip

        finally
            lock.unlock()
    }

    def waitUntilFull(): Unit = {
        lock.lock()
        try 
            // car waits until it is full to start a ride
            while (!readyForTrip)
                roomToStartTrip.await()
            log("        Car is full!!! Starting the ride....")
        
        finally
            lock.unlock()
    }

    def endRide(): Unit =  {
        lock.lock()
        try 
            // car signals that the ride has ended
            log("        Ride finished... :-(")
            readyForTrip = false
            exitDoor = true
            roomToExit.signalAll() // signal everybody to exit

        finally
            lock.unlock()
    }

    override def run(): Unit = {

        try
            while (true) {
                waitUntilFull()
                Thread.sleep(Random.nextInt(500)) // simulate the ride
                endRide()
            }
        catch
            case  e : InterruptedException => log("I go home")
    }

}

object Exercise4_locks {
    def main(args: Array[String]): Unit = {
        val car = new Car(5)
        val passengers = new Array[Thread](20)

        car.start()

        for (i <- passengers.indices)
            passengers(i) = thread {
                Thread.sleep(500) // simulate walking around the funfair
                car.takeRide(i)
            }

        passengers.foreach(_.join())
        car.interrupt()
        log("End of the program")
    }

}
