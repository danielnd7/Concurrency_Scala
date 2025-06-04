import scala.util.Random
import Concurrency.*

import java.util.concurrent.locks.ReentrantLock

object Boat_locks:

    @volatile private var nIPhone = 0
    @volatile private var nAndroid = 0
    @volatile private var iphoneGateOpen = true
    @volatile private var androidGateOpen = true
    @volatile private var exitGate = false

    private val lock = ReentrantLock(true)
    private val roomIphone = lock.newCondition()
    private val roomAndroid = lock.newCondition()
    private val roomExit = lock.newCondition()


    def tripIPhone(id: Int): Unit = {
        lock.lock()
        try

            while (!iphoneGateOpen)
                roomIphone.await()

            nIPhone += 1
            log(s"IPhone student $id boards the boat. Current: iPhone=$nIPhone, Android=$nAndroid")

            if (nAndroid == 2 && nIPhone == 1)
                androidGateOpen = false
            else if (nIPhone == 2 && nAndroid == 1)
                iphoneGateOpen = false
            else if (nIPhone == 3) // case of 3 IPhones
                androidGateOpen = false


            if (nIPhone + nAndroid == 4) { // case of the forth passenger
                androidGateOpen = false
                iphoneGateOpen = false
                //log("EVERYTHING CLOSED")

                log("Starting the trip....")
                Thread.sleep(Random.nextInt(200))
                log("Trip finished....")

                exitGate = true
                roomExit.signalAll()

            } else {
                while (!exitGate)
                    roomExit.await()
            }


            nIPhone -= 1
            log(s"IPhone student $id disembarks. Remaining: iPhone=$nIPhone, Android=$nAndroid")

            if (nAndroid + nIPhone == 0)
                exitGate = false
                iphoneGateOpen = true
                androidGateOpen = true

                roomAndroid.signalAll()
                roomIphone.signalAll()

        finally
            lock.unlock()
    }

    def tripAndroid(id: Int): Unit = {
        lock.lock()
        try

            while (!androidGateOpen)
                roomAndroid.await()

            nAndroid += 1
            log(s"Android student $id boards the boat. Current: iPhone=$nIPhone, Android=$nAndroid")

            // same for android and iphone
            if (nAndroid == 2 && nIPhone == 1)
                androidGateOpen = false
            else if (nIPhone == 2 && nAndroid == 1)
                iphoneGateOpen = false
            else if (nAndroid == 3) // case of 3 Androids
                iphoneGateOpen = false
            //log(s"----- gateIphone: $iphoneGateOpen, gateAndroid: $androidGateOpen")


            if (nIPhone + nAndroid == 4) { // case of the forth passenger
                androidGateOpen = false
                iphoneGateOpen = false
                //log("EVERYTHING CLOSED")

                log("Starting the trip....")
                Thread.sleep(Random.nextInt(200))
                log("Trip finished....")

                exitGate = true
                roomExit.signalAll()

            } else {
                while (!exitGate)
                    roomExit.await()
            }


            nAndroid -= 1
            log(s"Android student $id disembarks. Remaining: iPhone=$nIPhone, Android=$nAndroid")

            if (nAndroid + nIPhone == 0) // the last one resets everything
                exitGate = false
                iphoneGateOpen = true
                androidGateOpen = true

                roomAndroid.signalAll()
                roomIphone.signalAll()

        finally
            lock.unlock()
    }

object Exercise5_locks:

    def main(args: Array[String]): Unit =
        val totalIPhones = 10
        val totalAndroids = 10

        val iphones = Array.tabulate(totalIPhones)(i =>
            thread:
                Thread.sleep(Random.nextInt(400))
                Boat_locks.tripIPhone(i)
        )

        val androids = Array.tabulate(totalAndroids)(i =>
            thread:
                Thread.sleep(Random.nextInt(400))
                Boat_locks.tripAndroid(i)
        )

