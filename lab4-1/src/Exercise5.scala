import Concurrency.{parallel, thread}

import scala.util.Random

def merge(l1: List[Int], l2: List[Int]): List[Int] =
    var sortedLst: List[Int] = List.empty[Int]
    var counter1 = 0
    var counter2 = 0

    while (counter1 < l1.length && counter2 < l2.length)
        if (l1(counter1) < l2(counter2))
            sortedLst = l1(counter1) :: sortedLst // prepending because of the efficiency
            counter1 += 1
        else
            sortedLst = l2(counter2) :: sortedLst
            counter2 += 1

    // adding the remaining elements from the non empty list:
    while (counter1 < l1.length)
        sortedLst = l1(counter1) :: sortedLst // prepending because of the efficiency
        counter1 += 1

    while (counter2 < l2.length)
        sortedLst = l2(counter2) :: sortedLst
        counter2 += 1


    sortedLst.reverse // reverse is used because of the prepending before


def sort(l: List[Int]): List[Int] =
    if (l.length > 1)
        val (l1, l2) = l.splitAt(l.length / 2)
        val (sortedL1, sortedL2) = parallel(sort(l1), sort(l2))
        merge(sortedL1, sortedL2)
    else
        l



object Exercise5 :
    def main(args: Array[String]) =
        val testList = List.fill(15)(Random.nextInt(100))
        println("main: " + testList)
        println("sol : " + sort(testList))


//        testing merge method:
//        val l1 = List(1,3,5,10,11,23,26)
//        val l2 = List(2,4,7,9,12,15,17,18,30)
//        println(merge(l1, l2))






