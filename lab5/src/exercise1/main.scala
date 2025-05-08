package exercise1

import Concurrency.*

import scala.util.Random

class Peterson :
    @volatile var t1WantsCS = false
    @volatile var t0WantsCS = false
    @volatile var turn = 0

    def enter0() =
        t0WantsCS = true
        turn = 1
        while (t1WantsCS && turn == 1)
            Thread.sleep(0)

    def exit0() =
        t0WantsCS = false

    def enter1() =
        t1WantsCS = true
        turn = 0
        while (t0WantsCS && turn == 0)
            Thread.sleep(0)

    def exit1() =
        t1WantsCS = false



object Buffer :
    val N = 20
    var elem = new Array[Int](N)
    var idxP = 0 // next position to put
    var idxC = 0 // next position to take
    @volatile var nElem = 0 // num of valid elems in the buffer

    val mutex = new Peterson

    def store(data: Int) =
        while(nElem == N) Thread.sleep(0)
        mutex.enter0()
        elem(idxP) = data // store the data

        println(s"${Thread.currentThread.getName}: put -> $data")

        idxP = (idxP + 1) % N // increment the position
        nElem += 1
        mutex.exit0()

    def take(): Int =
        while (nElem == 0) Thread.sleep(0)
        mutex.enter1()
        val data = elem(idxC) // take

        println(s"${Thread.currentThread.getName}: take <- $data") // log(s": take <- $data")

        idxC = (idxC + 1) % N
        nElem -= 1
        mutex.exit1()
        data


@main
def main(): Unit = {
    val totalElems = 50

    val prod = thread {
        for (i <- 0 until totalElems)
            Thread.sleep(Random.nextInt(300))
            Buffer.store(i)
    }
    val cons = thread {
        for (i <- 0 until totalElems)
            val data = Buffer.take()
            println(data)
            Thread.sleep(Random.nextInt(500))
    }
    prod.join()
    cons.join()
}