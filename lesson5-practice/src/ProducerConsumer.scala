import Concurrency._
import java.util.Random


object Variable :
    @volatile private var isData: Boolean = false
    private var value: Int = 0;
    def write(nvalue: Int) =
        while (isData) Thread.sleep(0)

        value = nvalue
        isData = true
    def read =
        while (!isData) Thread.sleep(0)

        val aux = value
        isData = false
        aux

object ProdCons extends App :
    val prod = thread {
        val rnd = new Random
        for (i <- 0 until 10)
            val item = Math.abs(rnd.nextInt%100)
            Variable.write(item)
            log(s"Producer: $item")
    }

    val cons = thread {
        for (i <- 0 until 10)
            log(s"Consumer:  ${Variable.read}")
    }


