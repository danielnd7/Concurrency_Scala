// can be imported as Concurrency.parallel...
def thread(body: => Unit): Thread =
        val thr = new Thread {
                override def run() = body
        }
        thr.start()
        thr

def fibonacci(n:Int):(Int,Int) =
        @volatile var prevFib: Option[(Int, Int)] = None

        if (n == 1)
                println(s"${Thread.currentThread.getName}: fib($n) = 1")
                (1, 0)
        else
                // create a new thread
                val newThread = thread {
                        prevFib = Some(fibonacci(n - 1))
                }
                // wait until it finishes
                newThread.join()

                val nFib = prevFib.get._1 + prevFib.get._2
                println(s"${Thread.currentThread.getName}: fib($n) = $nFib")
                (nFib , prevFib.get._1)


// VERSION-2 USING class Thread  -------------------------
class FibonacciThread(body: => Unit) extends Thread {
        override def run() = body
}
def fibonacci_1(n:Int):(Int,Int) =
        @volatile var prevFib: Option[(Int, Int)] = None

        if (n == 1)
                println(s"${Thread.currentThread.getName}: fib($n) = 1")
                (1, 0)
        else
                // create a new thread
                val thr1 = new FibonacciThread({prevFib = Some(fibonacci(n - 1))}) // problematic line
                thr1.start()
                thr1.join()

                val nFib = prevFib.get._1 + prevFib.get._2
                println(s"${Thread.currentThread.getName}: fib($n) = $nFib")
                (nFib, prevFib.get._1)


object Exercise4 :
        def main(args: Array[String]) =
                val n = 7
                fibonacci(n)
                println(s"${Thread.currentThread.getName}: End of the program")

                println("-----------------")
                println("Using fibonacci_1 with an additional FibonacciThread class:")
                fibonacci_1(n)
                println(s"${Thread.currentThread.getName}: End of the program")

