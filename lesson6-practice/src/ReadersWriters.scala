import Concurrency.{log, thread}

import java.util.concurrent.Semaphore
import scala.util.Random

class DBMG :
    private val writing = new Semaphore(1, true)
    private var nReaders = 0
    private val mutexNRead = Semaphore(1, true)

    private var nWriters = 0
    private val reading = new Semaphore(1, true)
    private val mutexNWrit = new Semaphore(1,true)
    private val mutex3 = new Semaphore(1, true)


    def enterWriter(id: Int) = {
        mutexNWrit.acquire()
        nWriters += 1
        if (nWriters == 1)
            reading.acquire()
        mutexNWrit.release()

        writing.acquire()
        log(s"Writer $id enters inside the DB")
    }

    def exitWriter(id: Int) = {
        mutexNWrit.acquire()
        nWriters -= 1
        if (nWriters == 0)
            reading.release()
        mutexNWrit.release()

        writing.release()
        log(s"Writer $id exits from the DB")
    }

    def enterReader(id: Int) =
        reading.acquire()
        mutexNRead.acquire()
        nReaders += 1
        if (nReaders == 1)
            writing.acquire()
        log(s"Reader $id enters into the DB. There are $nReaders")
        mutexNRead.release()
        reading.release()

    def exitReader(id: Int) =
        mutexNRead.acquire()
        nReaders -= 1
        if (nReaders == 0)
            writing.release()
        log(s"Reader $id exits from the DB. There are $nReaders")
        mutexNRead.release()


object ReadersWriters :
    val mng = new DBMG
    def main() =
        val NR = 10
        val NW = 2
        val reader= new Array[Thread](NR)
        val writer= new Array[Thread](NW)
        for(i<-0 until(reader.length)){
            reader(i) = thread{
                while(true) {
                    mng.enterReader(i)
                    // Reader i insidetheDB
                    Thread.sleep(Random.nextInt(200))
                    mng.exitReader(i)
                }
            }
        }
        for (i <- 0 until (writer.length)) {
            writer(i) = thread {
                while (true) {
                    mng.enterWriter(i)
                    // Writeri insidetheDB
                    Thread.sleep(Random.nextInt(200))
                    mng.exitWriter(i)
                }
            }
        }