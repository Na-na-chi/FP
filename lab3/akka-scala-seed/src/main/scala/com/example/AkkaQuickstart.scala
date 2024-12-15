package com.example

import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.example.GreeterMain.SayHello

object Greeter {
  // Определяем сообщение Greet, которое включает имя человека для приветствия
  // и ссылку на актор, который должен получить ответ.
  final case class Greet(whom: String, replyTo: ActorRef[Greeted])

  // Определяем сообщение Greeted, которое включает имя того, кто был поприветствован,
  // и ссылку на актор, отправивший приветствие.
  final case class Greeted(whom: String, from: ActorRef[Greet])

  // Создаем поведение актора Greeter.
  def apply(): Behavior[Greet] = Behaviors.receive { (context, message) =>

    // Выводим приветствие в лог консоли.
    context.log.info("Hello {}!", message.whom)

    // Отправляем сообщение Greeted обратно отправителю с информацией о том, кто был поприветствован.
    message.replyTo ! Greeted(message.whom, context.self)

    // Возвращаем текущее поведение (без изменений).
    Behaviors.same
  }
}

// Актор, который отвечает на полученные приветствия
object GreeterBot {

  // Создаем поведение актора GreeterBot
  def apply(max: Int): Behavior[Greeter.Greeted] = {
    bot(0, max) // Начинаем с нуля приветствий.
  }

  // Определяем поведение бота для обработки сообщений Greeted.
  private def bot(greetingCounter: Int, max: Int): Behavior[Greeter.Greeted] =
    Behaviors.receive { (context, message) =>

      // Увеличиваем счетчик приветствий.
      val n = greetingCounter + 1

      // Логируем информацию о количестве полученных приветствий.
      context.log.info("Greeting {} for {}", n, message.whom)

      // Если достигнуто максимальное количество приветствий, останавливаем актор.
      if (n == max) {
        Behaviors.stopped
      } else {

        // Отправляем новое приветствие обратно актеру Greeter.
        message.from ! Greeter.Greet(message.whom, context.self)

        // Продолжаем работу с обновленным счетчиком.
        bot(n, max)
      }
    }
}

// Актор, который управляет взаимодействием других акторов
object GreeterMain {

  // Определяем сообщение SayHello, которое содержит имя для приветствия.
  final case class SayHello(name: String)

  // Создаем поведение основного актора.
  def apply(): Behavior[SayHello] =
    Behaviors.setup { context =>

      // Создаем актор Greeter и сохраняем ссылку на него.
      val greeter = context.spawn(Greeter(), "greeter")

      // Обрабатываем входящие сообщения SayHello.
      Behaviors.receiveMessage { message =>

        // Создаем нового бота GreeterBot для обработки приветствий.
        val replyTo = context.spawn(GreeterBot(max = 3), message.name)

        // Отправляем приветствие от основного актора к Greeter с указанием нового бота как получателя ответа.
        greeter ! Greeter.Greet(message.name, replyTo)
        Behaviors.same // Возвращаем текущее поведение.
      }
    }
}

object AkkaQuickstart extends App {

  // Создаем систему актеров с поведением GreeterMain
  val greeterMain: ActorSystem[GreeterMain.SayHello] =
    ActorSystem(GreeterMain(), "AkkaQuickStart")

  // Отправляем сообщение SayHello с именем "Charles" в систему актеров.
  greeterMain ! SayHello("Charles")

}
