import scala.util.{Try, Success, Failure}
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
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

  //  Option
  def goodEnoughPasswordOption(password: String): Boolean = {
    val hasMinLength = password.length >= 8
    val hasUpperCase = password.exists(_.isUpper)
    val hasLowerCase = password.exists(_.isLower)
    val hasDigit = password.exists(_.isDigit)
    val hasSpecialChar = password.exists(c => "!@#$%^&*()-_+=<>?".contains(c))

    hasMinLength && hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar
  }

  //  Try
  def goodEnoughPasswordTry(password: String): Either[Boolean, String] = {
    Try {
      val issues = List(
        if (password.length >= 8) None
        else Some("Пароль должен содержать не менее 8 символов"),
        if (password.exists(_.isUpper)) None
        else Some("Пароль должен содержать хотя бы одну заглавную букву"),
        if (password.exists(_.isLower)) None
        else Some("Пароль должен содержать хотя бы одну строчную букву"),
        if (password.exists(_.isDigit)) None
        else Some("Пароль должен содержать хотя бы одну цифру"),
        if (password.exists(c => "!@#$%^&*()-_+=<>?".contains(c))) None
        else Some("Пароль должен содержать хотя бы один спецсимвол")
      ).flatten

      if (issues.isEmpty) Left(true)
      else Right(issues.mkString(", "))
    } match {
      case Success(result) => result
      case Failure(_)      => Right("Не удалось обработать пароль")
    }
  }

  // Future
  def readPasswordFuture(): Future[String] = Future {
    var password = ""
    var valid = false
    while (!valid) {
      println("Введите пароль:")
      password = StdIn.readLine()
      goodEnoughPasswordTry(password) match {
        case Left(true) =>
          println("Пароль принят!")
          valid = true
        case Right(issues) =>
          println(s"Ошибки: $issues")
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
    println(
      s"Результат численного интегрирования (f(x) = x^2 на [0, 1]): $integralResult"
    )

    // Option
    println("\nOption:")
    val password1 = "P@ssw0rd!"
    val password2 = "simplepass"
    println(
      s"Пароль '$password1' корректен: ${goodEnoughPasswordOption(password1)}"
    )
    println(
      s"Пароль '$password2' корректен: ${goodEnoughPasswordOption(password2)}"
    )

    // Try
    println("\nTry:")
    List(password1, password2).foreach { password =>
      goodEnoughPasswordTry(password) match {
        case Left(true) => println(s"Пароль '$password' корректен")
        case Right(issues) =>
          println(s"Пароль '$password' некорректен: $issues")
        case Left(false) =>
          println(s"Ошибка при проверке пароля '$password'")
      }
    }

    // Future
    println("\nFuture:")
    val passwordFuture = readPasswordFuture()
    val finalPassword = Await.result(passwordFuture, Duration.Inf)
    println(s"Полученный пароль: $finalPassword")
  }
}
