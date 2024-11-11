file:///C:/Users/epobi/Desktop/Predmeti/Fp/lab1/src/main/scala/Main.scala
### dotty.tools.dotc.MissingCoreLibraryException: Could not find package scala from compiler core libraries.
Make sure the compiler core libraries are on the classpath.
   

occurred in the presentation compiler.

presentation compiler configuration:


action parameters:
offset: 498
uri: file:///C:/Users/epobi/Desktop/Predmeti/Fp/lab1/src/main/scala/Main.scala
text:
```scala
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
  // 5 Задание@@
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

```



#### Error stacktrace:

```
dotty.tools.dotc.core.Denotations$.select$1(Denotations.scala:1321)
	dotty.tools.dotc.core.Denotations$.recurSimple$1(Denotations.scala:1349)
	dotty.tools.dotc.core.Denotations$.recur$1(Denotations.scala:1351)
	dotty.tools.dotc.core.Denotations$.staticRef(Denotations.scala:1355)
	dotty.tools.dotc.core.Symbols$.requiredPackage(Symbols.scala:943)
	dotty.tools.dotc.core.Definitions.ScalaPackageVal(Definitions.scala:215)
	dotty.tools.dotc.core.Definitions.ScalaPackageClass(Definitions.scala:218)
	dotty.tools.dotc.core.Definitions.AnyClass(Definitions.scala:281)
	dotty.tools.dotc.core.Definitions.syntheticScalaClasses(Definitions.scala:2161)
	dotty.tools.dotc.core.Definitions.syntheticCoreClasses(Definitions.scala:2176)
	dotty.tools.dotc.core.Definitions.init(Definitions.scala:2192)
	dotty.tools.dotc.core.Contexts$ContextBase.initialize(Contexts.scala:921)
	dotty.tools.dotc.core.Contexts$Context.initialize(Contexts.scala:544)
	dotty.tools.dotc.interactive.InteractiveDriver.<init>(InteractiveDriver.scala:41)
	dotty.tools.pc.MetalsDriver.<init>(MetalsDriver.scala:32)
	dotty.tools.pc.ScalaPresentationCompiler.newDriver(ScalaPresentationCompiler.scala:99)
```
#### Short summary: 

dotty.tools.dotc.MissingCoreLibraryException: Could not find package scala from compiler core libraries.
Make sure the compiler core libraries are on the classpath.
   