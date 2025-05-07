package object Concurrency {

    def log(message: String): Unit =
        println(s"${Thread.currentThread.getName}: $message")

    def thread(body: Unit): Thread =
        val thr = new Thread {
            override def run() = body
        }
        thr.start()
        thr


    def parallelDemo[A, B](a: => A, b: => B): (A, B) =
        var solA: A = null.asInstanceOf[A]
        var solB: B = null.asInstanceOf[B]

        val threadA = thread {
            solA = a
        }
        val threadB = thread {
            solB = b
        }
        threadA.join()
        threadB.join()

        (solA, solB)


    def parallel[A, B](a: => A, b: => B): (A, B) =
        var solA: Option[A] = None
        var solB: Option[B] = None

        val threadA = thread {
            solA = Some(a)
        }
        val threadB = thread {
            solB = Some(b)
        }
        threadA.join()
        threadB.join()

        (solA.get, solB.get) // returned tuple
}
