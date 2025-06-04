import scala.util.Random
import Concurrency._

class Car(capacity: Int) extends Thread {
    // CS-passenger1: if the car is full, a passenger cannot board until the trip is over
    // and the current passengers have gotten off
    // CS-passenger2: a passenger in the car cannot get off until the ride ends
    // CS-car: the car waits until C passengers have boarded before starting a ride

    private var numPassengers = 0
    private var entryDoor = true
    private var exitDoor = false
    private var readyForTrip = false

    def takeRide(id: Int): Unit = synchronized {
        // passenger id wants to take a ride on the roller coaster
        while (!entryDoor)
            wait()

        numPassengers += 1
        log(s"Passenger $id boards the car. Total: $numPassengers passengers.")

        if (numPassengers == capacity)
            entryDoor = false
            readyForTrip = true
            notify()

        while (!exitDoor)
            wait()

        numPassengers -= 1
        log(s"Passenger $id exits the car. Remaining: $numPassengers passengers.")

        if (numPassengers == 0)
            exitDoor = false
            entryDoor = true
            notifyAll()
    }

    def waitUntilFull(): Unit = synchronized {
        // car waits until it is full to start a ride
        while (!readyForTrip)
            wait()
        log("        Car is full!!! Starting the ride....")
    }

    def endRide(): Unit = synchronized {
        // car signals that the ride has ended
        log("        Ride finished... :-(")
        readyForTrip = false
        exitDoor = true
        notifyAll()
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

object Exercise4 {
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
