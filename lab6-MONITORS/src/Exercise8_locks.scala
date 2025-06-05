//import java.util.concurrent.*
import lab6.{log, thread}

import java.util.concurrent.locks.ReentrantLock
import scala.util.Random

class Cauldron_locks(R:Int){
    //CS-cannibal i: cannot take a portion from the portionsAvailable, if empty
    //CS-cook: cannot cook a ner explorer until the portionsAvailable is empty
    @volatile private var portionsAvailable = R // Initially full

    private val lock = new ReentrantLock(true)
    private val roomCannibals = lock.newCondition()
    private val roomCook = lock.newCondition()

    def takeAPortion(i:Int)= {
        lock.lock()
        try
            while (portionsAvailable == 0)
                roomCook.signal() // notify the cook
                roomCannibals.await()
    
            //cannibal i takes portion from the portionsAvailable
            portionsAvailable -= 1
            log(s"cannibal $i takes portion from the portionsAvailable. Remaining $portionsAvailable portions.")

        finally
            lock.unlock()
    }

    def sleep = {
        //cook waits for the portionsAvailable to be empty

        // NOT NEEDED if implemented with monitors/locks
    }
    def fillCauldron = {
        lock.lock()
        try
            while (portionsAvailable > 0) // sleep
                roomCook.await()
    
            portionsAvailable = R
            roomCannibals.signalAll()
    
            log(s"The cook fills the portionsAvailable. Remaining $portionsAvailable portions.")
        
        finally
            lock.unlock()

    }
}
object Exercise8_locks {

    def main(args:Array[String]):Unit = {
        val NCan = 20
        val cauldron = new Cauldron_locks(5)
        val cannibal = new Array[Thread](NCan)
        for (i<-cannibal.indices)
            cannibal(i) = thread {
                while (true){
                    Thread.sleep(Random.nextInt(500))
                    cauldron.takeAPortion(i)
                }
            }
        val cook = thread{
            while (true){
                cauldron.sleep
                Thread.sleep(500)//cocinando
                cauldron.fillCauldron
            }
        }
    }
}

