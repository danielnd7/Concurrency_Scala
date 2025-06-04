import scala.util.Random
import Concurrency._

object WaterManager:

    private var doorH = true
    private var doorO = true
    private var doorExit = false

    private var numH = 0
    private var numO = 0

    def hReady(id: Int): Unit = synchronized {
        while (!doorH)
            wait()

        numH += 1
        log(s"Hydrogen $id is ready.")

        if (numH == 2)
            doorH = false

        if (numH + numO == 3)
            log(s"      Molecule formed! (H₂O)")
            doorExit = true
            notifyAll()

        while (!doorExit)
            wait()

        numH -= 1

        if (numO + numH == 0) // the last one exiting
            doorO = true
            doorH = true
            doorExit = false
            notifyAll()
    }

    def oReady(id: Int): Unit = synchronized {
        while (!doorO)
            wait()

        doorO = false
        numO += 1
        log(s"Oxygen $id is ready.")

        if (numH + numO == 3)
            log(s"      Molecule formed! (H₂O)")
            doorExit = true
            notifyAll()

        while (!doorExit)
            wait()

        numO -= 1

        if (numO + numH == 0) // the last one exiting
            doorO = true
            doorH = true
            doorExit = false
            notifyAll()
    }

object Exercise8:

    def main(args: Array[String]): Unit =
        val N = 5  // Number of water molecules to form
        val hydrogens = Array.tabulate(2 * N)(i =>
            thread:
                Thread.sleep(Random.nextInt(500))
                WaterManager.hReady(i)
        )
        val oxygens = Array.tabulate(N)(i =>
            thread:
                Thread.sleep(Random.nextInt(500))
                WaterManager.oReady(i)
        )

        hydrogens.foreach(_.join())
        oxygens.foreach(_.join())

        println("End of Program.")
