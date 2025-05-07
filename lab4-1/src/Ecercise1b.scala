class MyThread2(val myId: Int, val c: Char) extends Thread {
    override def run() =
        while (true)
            while (myId != Ecercise1b.turn)
                Thread.sleep(0)

            for (i <- 0 to myId)
                print(c)

            Ecercise1b.turn = (Ecercise1b.turn + 1) % 3

}

object Ecercise1b :
    var turn = 0 // the spot is important

    def main(args: Array[String]) =
        val thread1 = new MyThread2(0, 'A')
        val thread2 = new MyThread2(1, 'B')
        val thread3 = new MyThread2(2, 'C')

        thread1.start()
        thread2.start()
        thread3.start()

