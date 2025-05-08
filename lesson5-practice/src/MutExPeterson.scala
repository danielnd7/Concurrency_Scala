import Concurrency._
//  PETERSON'S ALGORITHM
object MutExPeterson :
    def main(args: Array[String]) =
        // Sharedvariables
        // VOLATILE is crucial
        @volatile var n  = 0

        @volatile var t1WantsCS = false
        @volatile var t2WantsCS = false
        @volatile var turn = 1

        val t1 = thread {
            for (i <- 0 until 10)
                // PREPROTOCOL:
                t1WantsCS = true
                turn = 2
                while (t2WantsCS && turn == 2) Thread.sleep(0)

                n += 1 //CS

                // POSTPROTOCOL
                t1WantsCS = false

        }

        val t2 = thread {
            for (i <- 0 until 10)
                t2WantsCS = true
                turn = 1
                while (t1WantsCS && turn == 1) Thread.sleep(0)

                n += 1  //CS

                t2WantsCS = false
        }

        t1.join()
        t2.join()
        log(s"n= $n") // n should be 20
