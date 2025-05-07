class MyThread(val t: Int, val c: Char) extends Thread{
    override def run() =
        for (i <- 0 until t)
            print(c)
}

object Exercise1a:
    def main(args: Array[String]) =
        val thread1 = new MyThread(10, 'A')
        val thread2 = new MyThread(15, 'B')
        val thread3 = new MyThread(20, 'C')
        
        thread1.start()
        thread2.start()
        thread3.start()
        
        println("  ## end of the program ##  ")