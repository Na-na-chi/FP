import scala.util.{Try, Success, Failure}
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.io.StdIn

object Lab2 {

  // Интегрирование
  def integral(
      f: Double => Double,
      l: Double,
      r: Double,
      steps: Int
  ): Double = {
    val stepSize = (r - l) / steps
    (0 until steps).map { i =>
      val x = l + i * stepSize
      f(x) * stepSize
    }.sum
  }

  // Option
  def goodEnoughPasswordOption(password: String): Boolean = {
    val conditions = List(
      password.length >= 8,
      password.exists(_.isUpper),
      password.exists(_.isLower),
      password.exists(_.isDigit),
      password.exists(c => "!@#$%^&*()-_+=<>?".contains(c))
    )

    conditions.reduce(_ && _)
  }

  // Try
  def goodEnoughPasswordTry(password: String): Either[Boolean, String] = {
    Try {
      val issues = List(
        (password.length >= 8, "Пароль должен содержать не менее 8 символов"),
        (password.exists(_.isUpper), "Пароль должен содержать хотя бы одну заглавную букву"),
        (password.exists(_.isLower), "Пароль должен содержать хотя бы одну строчную букву"),
        (password.exists(_.isDigit), "Пароль должен содержать хотя бы одну цифру"),
        (password.exists(c => "!@#$%^&*()-_+=<>?".contains(c)), "Пароль должен содержать хотя бы один спецсимвол")
      )

      val errorMessages = issues.collect { case (false, message) => message }

      if (errorMessages.isEmpty) Left(true)
      else Right(errorMessages.mkString(", "))
    } match {
      case Success(result) => result
      case Failure(_) => Right("Не удалось обработать пароль")
    }
  }

  // Future
  def readPasswordFuture(): Future[String] = Future {
    var valid = false
    var password = ""

    while (!valid) {
      println("Введите пароль:")
      password = StdIn.readLine()

      goodEnoughPasswordTry(password) match {
        case Left(true) =>
          println("Пароль принят!")
          valid = true
        case Right(issues) =>
          // Обработка ошибок с использованием map и формирование сообщения
          val errorMessages = issues.split(", ").map(issue => s"- $issue").mkString("\n")
          println(s"Ошибки:\n$errorMessages")
        case Left(false) =>
          println("Произошла ошибка при проверке пароля.")
      }
    }

    password
  }

  def main(args: Array[String]): Unit = {
    // Интегрирование
    println("Интегрирование:")
    def f(x: Double): Double = x * x
    val integralResult = integral(f, 0, 1, 100)
    println(s"Результат численного интегрирования (f(x) = x^2 на [0, 1]): $integralResult")

    // Option
    println("\nOption:")
    val password1 = "P@ssw0rd!"
    val password2 = "simplepass"
    println(s"Пароль '$password1' корректен: ${goodEnoughPasswordOption(password1)}")
    println(s"Пароль '$password2' корректен: ${goodEnoughPasswordOption(password2)}")

    // Try
    println("\nTry:")
    List(password1, password2).foreach { password =>
      goodEnoughPasswordTry(password) match {
        case Left(true) => println(s"Пароль '$password' корректен")
        case Right(issues) => println(s"Пароль '$password' некорректен: $issues")
        case Left(false) => println(s"Ошибка при проверке пароля '$password'")
      }
    }

    // Future
    println("\nFuture:")
    val passwordFuture = readPasswordFuture()
    val finalPassword = Await.result(passwordFuture, Duration.Inf)
    println(s"Полученный пароль: $finalPassword")
  }
}
