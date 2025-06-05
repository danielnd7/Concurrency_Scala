//import java.util.concurrent.*
import lab6.{log, thread}

import scala.util.Random

class Cauldron(R:Int){
    //CS-cannibal i: cannot take a portion from the portionsAvailable, if empty
    //CS-cook: cannot cook a ner explorer until the portionsAvailable is empty
    private var portionsAvailable = R // Initially full

    def takeAPortion(i:Int)= synchronized {
        while (portionsAvailable == 0)
            notifyAll() // notify the cook
            wait()

        //cannibal i takes portion from the portionsAvailable
        portionsAvailable -= 1
        log(s"cannibal $i takes portion from the portionsAvailable. Remaining $portionsAvailable portions.")

    }

    def sleep = synchronized {
        //cook waits for the portionsAvailable to be empty

        // NOT NEEDED if implemented with monitors/locks
    }
    def fillCauldron = synchronized {
        while (portionsAvailable > 0) // sleep
            wait()

        portionsAvailable = R
        notifyAll()

        log(s"The cook fills the portionsAvailable. Remaining $portionsAvailable portions.")

    }
}
object Exercise8_monitors {

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

