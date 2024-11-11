object Lab1 {
  // 1 Задание
  @main def hello(): Unit = println("hello world")
  // 2 Задание
  printHello { 5 }
  // 3.1 Задание
  val numbers = List(1, 2, 3, 4, 5, 6)
  val (evens, odds) = splitByIndex(numbers)
  println(s"Нечетные индексы: $evens, Четные индексы: $odds")
  // 3.2 Задание
  val maxElement = findMax(List(1, 3, 7, 2, 5))
  println(s"Максимальный элемент: $maxElement")
  // 4 Задание
  println(greetingFunction)
  println(greetingFunction("Scala"))
  // 5 Задание
  println(typeCheck(5))
  println(typeCheck("Scala"))
  println(typeCheck(3.14))
  // 6 Задание
  val addOne = (x: Int) => x + 1
  val multiplyByTwo = (x: Int) => x * 2

  val composedFunction = compose(addOne, multiplyByTwo)
  println(composedFunction(3)) // Результат: (3 * 2) + 1 = 7
}
// 2 Задание
def printHello(n: Int): Unit = {
  for (i <- 0 until n) {
    val x = if (i % 2 == 0) i else n - i
    println(s"hello $x")
  }
}
// 3.1 Задание
def splitByIndex(xs: List[Int]): (List[Int], List[Int]) = {
  xs.zipWithIndex.partition(_._2 % 2 == 0) match {
    case (evens, odds) => (evens.map(_._1), odds.map(_._1))
  }
}
// 3.2 Задание
def findMax(xs: List[Int]): Int = xs.reduceLeft(_ max _)
// 4 Задание
val greetingFunction = (name: String) => s"Hello, $name"
// 5 Задание
def typeCheck(x: Any): String = x match {
  case i: Int    => s"$i is an Integer"
  case s: String => s"$s is a String"
  case _         => "Unknown type"
}
// 6 Задание
def compose[A, B, C](f: B => C, g: A => B): A => C = x => f(g(x))
