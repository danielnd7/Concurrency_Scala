import Concurrency.{log, thread}

import java.util.concurrent.*
import scala.util.Random

class Cauldron(R:Int){
    //CS-cannibal i: cannot take a portion from the Cauldron, if empty
    //CS-cook: cannot cook a ner explorer until the Cauldron is empty
    private var numAvailablePortions = R // Initially full
    private val mutex = new Semaphore(1, true)
    private val portionsAvailable = new Semaphore(0, true) // 0= there is no available portions, 1= there are
    private val cook = new Semaphore(0, true) // 0= no need to cook , 1= wakes up the cook


    def takeAPortion(i:Int)={
        //cannibal i takes portion from the Cauldron
        mutex.acquire()

        if (numAvailablePortions == 0)
            cook.release() // wake up the cook
            portionsAvailable.acquire() // wait for cook to finish cooking

        numAvailablePortions -= 1
        log(s"cannibal $i takes portion from the Cauldron. Remaining $numAvailablePortions portions.")

        mutex.release()
    }

    def sleep = {
        //cook waits for the Cauldron to be empty
        cook.acquire()
    }
    def fillCauldron = {
        numAvailablePortions = R // it is safe because the waiting cannibal is holding the mutex
        log(s"The cook fills the Cauldron. Remaining $numAvailablePortions portions.")
        portionsAvailable.release()
    }
}
object Exercise8 {

    def main(args:Array[String]):Unit = {
        val NCan = 20
        val Cauldron = new Cauldron(5)
        val cannibal = new Array[Thread](NCan)
        for (i<-cannibal.indices)
            cannibal(i) = thread {
                while (true){
                    Thread.sleep(Random.nextInt(500))
                    Cauldron.takeAPortion(i)
                }
            }
        val cook = thread{
            while (true){
                Cauldron.sleep
                Thread.sleep(500)//cocinando
                Cauldron.fillCauldron
            }
        }
    }
}

