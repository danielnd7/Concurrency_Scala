import Concurrency.{log, thread}

import java.util.concurrent.*
import scala.util.Random

class Car(C:Int) extends Thread {
    //CS-passenger1: if the Car is full, a passenger cannot board on it until the trip has finished
    // and all the car's passenger have get out
    //CS-passenger2: a passenger on the Car cannot get out until the trip has finished
    //CS-Car: the Car waits until C passengers are onboard to start a trip
    @volatile private var numPas = 0
    private val readyForRide = new Semaphore(0, true)
    private val mutex = new Semaphore(1, true) // for accessing numPas
    private val enterDoor = new Semaphore(1, true)
    private val exitDoor = new Semaphore(0, true)


    def newTrip(id:Int)={
        // the passenger id wants to use the roller coaster
        enterDoor.acquire()
        mutex.acquire()
        numPas += 1

        log(s"The passenger $id boards on the Car. There are $numPas passengers.")

        if (numPas == C) // the passenger is the last one
            readyForRide.release() // if the car is full, wake up the car rider
        else
            enterDoor.release() // if not the last, reopen the door
        mutex.release()
        
        exitDoor.acquire()

        mutex.acquire()
        numPas -= 1
        log(s"The passenger $id gets out the Car. There are $numPas passengers.")
        
        if (numPas == 0) // the last passenger going out does not reopen the exit door, but opens the enter one
            enterDoor.release()
        else
            exitDoor.release()

        mutex.release()
    }

    def waitsForFull = {
        readyForRide.acquire()
        // The Car waits to be full to start a trip

        log(s"        Car full!!! Let's begin the trip....")
    }

    def endTrip = {
        // The Car notifies the end of the trip

        exitDoor.release()
        log(s"        End of trip... :-(")

    }

    override def run = {
        while (true){
            waitsForFull
            Thread.sleep(Random.nextInt(Random.nextInt(500))) // The Car performs a trip
            endTrip
        }
    }
}
object Exercise4 {
    def main(args:Array[String])=
        val Car = new Car(5)
        val passenger = new Array[Thread](12)
        Car.start()
        for (i<-0 until passenger.length)
            passenger(i) = thread {
                while (true)
                    Thread.sleep(Random.nextInt(500))
                    Car.newTrip(i)
            }
}
