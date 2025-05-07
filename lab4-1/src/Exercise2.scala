def periodical(t: Long, b: => Unit) : Thread =
    new Thread {
        override def run() =
            while (true)
                Thread.sleep(t)
                b
    }

object Exercise2 :
    def main(args: Array[String]) =
        def b1 = println(s"Hi_i_am_${Thread.currentThread.getName}")
        periodical(1000, b1).start()
        periodical(3000, b1).start()

