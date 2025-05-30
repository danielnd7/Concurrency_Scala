import scala.util.Random
import Concurrency._

object Kindergarten:

    private var numBabies = 0
    private var numAdults = 0

    def babyEnters(id: Int): Unit = synchronized {
        while !(numBabies + 1 <= numAdults * 3) do wait()
        numBabies += 1
        log(s"Baby $id entered. Babies=$numBabies, Adults=$numAdults — OK=${numBabies <= 3 * numAdults}")
    }

    def babyLeaves(id: Int): Unit = synchronized {
        numBabies -= 1
        notifyAll()
        log(s"Baby $id left. Babies=$numBabies, Adults=$numAdults — OK=${numBabies <= 3 * numAdults}")
    }

    def adultEnters(id: Int): Unit = synchronized {
        numAdults += 1
        notifyAll()
        log(s"Adult $id entered. Babies=$numBabies, Adults=$numAdults — OK=${numBabies <= 3 * numAdults}")
    }

    def adultLeaves(id: Int): Unit = synchronized {
        while !(numBabies <= (numAdults - 1) * 3) do wait()
        numAdults -= 1
        log(s"Adult $id left. Babies=$numBabies, Adults=$numAdults — OK=${numBabies <= 3 * numAdults}")

    }

object Exercise7:

    def main(args: Array[String]): Unit =
        val totalBabies = 15
        val totalAdults = 5

        val babies = Array.tabulate(totalBabies)(i =>
            thread:
                while true do
                    Thread.sleep(Random.nextInt(700))
                    Kindergarten.babyEnters(i)
                    Thread.sleep(Random.nextInt(500))
                    Kindergarten.babyLeaves(i)
        )

        val adults = Array.tabulate(totalAdults)(i =>
            thread:
                while true do
                    Thread.sleep(Random.nextInt(700))
                    Kindergarten.adultEnters(i)
                    Thread.sleep(Random.nextInt(500))
                    Kindergarten.adultLeaves(i)
        )
