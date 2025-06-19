import Concurrency.{log, thread}

import java.util.concurrent.*
import scala.util.Random

object waterManager {
    //CS-Hyd1: The hydrogen wants to create a molecule. Waits if there are already hydrogens
    //CS-Hyd2: An hydrogen should wait the other two atoms to create a molecule
    //CS-Ox1: The oxygen wants to create a molecule. Waits if there are already any oxygen
    //CS-Ox2: The oxygen should wait the other two atoms to create a molecule
    private var numO = 0
    private var numH = 0
    private val mutex = new Semaphore(1, true) // to access the numO and numH
    private val enterOxygen = new Semaphore(1, true)
    private val enterHydrogen = new Semaphore(1, true)
    private val exitDoor = new Semaphore(0, true)

    def oxygen(id: Int) = {
        // The oxygen wants to create a molecule
        enterOxygen.acquire()
        mutex.acquire()
        numO += 1

        log(s"oxygen $id wants to create a molecule")

        if (numO + numH == 3)
            exitDoor.release()
            log(s"      Molecule created!!!")

        mutex.release()

        exitDoor.acquire()
        // log(s"Sale oxygen $id: numO: $numO---molecule=${molecule.availablePermits()}")

        mutex.acquire()
        numO -= 1
        if (numO + numH == 0)
            enterHydrogen.release()
            enterOxygen.release()
        else // if not the last reopen the exit door
            exitDoor.release()
        mutex.release()
    }

    def hydrogen(id: Int) = {
        // The hydrogen wants to create a molecule
        enterHydrogen.acquire()
        mutex.acquire()
        numH += 1

        log(s"hydrogen $id wants to create a molecule")

        if (numH == 1)
            enterHydrogen.release()

        if (numO + numH == 3)
            exitDoor.release()
            log(s"      Molecule created!!!")

        mutex.release()
        exitDoor.acquire()

        mutex.acquire()
        numH -= 1
        if (numO + numH == 0)
            enterHydrogen.release()
            enterOxygen.release()
        else // if not the last reopen the exit door
            exitDoor.release()
        mutex.release()

    }
}

object Exercise5 {

    def main(args: Array[String]) =
        val N = 5
        val hydrogen = new Array[Thread](2 * N)
        for (i <- 0 until hydrogen.length)
            hydrogen(i) = thread {
                Thread.sleep(Random.nextInt(500))
                waterManager.hydrogen(i)
            }
        val oxigeno = new Array[Thread](N)
        for (i <- 0 until oxigeno.length)
            oxigeno(i) = thread {
                Thread.sleep(Random.nextInt(500))
                waterManager.oxygen(i)
            }
        hydrogen.foreach(_.join())
        oxigeno.foreach(_.join())
        log("End of Program")
}
