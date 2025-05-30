import scala.util.Random
import Concurrency._

object Boat:

    private var nIPhone = 0
    private var nAndroid = 0
    private var iphoneGateOpen = true
    private var androidGateOpen = true
    private var exitGate = false

    def tripIPhone(id: Int): Unit = synchronized {
        while (!iphoneGateOpen)
            wait()

        nIPhone += 1
        log(s"IPhone student $id boards the boat. Current: iPhone=$nIPhone, Android=$nAndroid")

        if (nAndroid == 2 && nIPhone == 1)
            androidGateOpen = false
        else if (nIPhone == 2 && nAndroid == 1)
            iphoneGateOpen = false
        else if (nIPhone == 3) // case of 3 IPhones
            androidGateOpen = false
        //log(s"----- gateIphone: $iphoneGateOpen, gateAndroid: $androidGateOpen")


        if (nIPhone + nAndroid == 4) { // case of the forth passenger
            androidGateOpen = false
            iphoneGateOpen = false
            //log("EVERYTHING CLOSED")

            log("Starting the trip....")
            Thread.sleep(Random.nextInt(200))
            log("Trip finished....")

            exitGate = true
            notifyAll()

        } else {
            while (!exitGate)
                wait()
        }


        nIPhone -= 1
        log(s"IPhone student $id disembarks. Remaining: iPhone=$nIPhone, Android=$nAndroid")

        if (nAndroid + nIPhone == 0)
            exitGate = false
            iphoneGateOpen = true
            androidGateOpen = true
            notifyAll()
    }

    def tripAndroid(id: Int): Unit = synchronized {
        while (!androidGateOpen)
            wait()

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
            notifyAll()

        } else {
            while (!exitGate)
                wait()
        }


        nAndroid -= 1
        log(s"Android student $id disembarks. Remaining: iPhone=$nIPhone, Android=$nAndroid")

        if (nAndroid + nIPhone == 0) // the last one resets everything
            exitGate = false
            iphoneGateOpen = true
            androidGateOpen = true
            notifyAll()
    }

object Exercise5:

    def main(args: Array[String]): Unit =
        val totalIPhones = 10
        val totalAndroids = 10

        val iphones = Array.tabulate(totalIPhones)(i =>
            thread:
                Thread.sleep(Random.nextInt(400))
                Boat.tripIPhone(i)
        )

        val androids = Array.tabulate(totalAndroids)(i =>
            thread:
                Thread.sleep(Random.nextInt(400))
                Boat.tripAndroid(i)
        )

