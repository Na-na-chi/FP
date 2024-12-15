import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import scala.util.Random

// Определяем общий интерфейс для добавляемых типов
sealed trait Addable
case class IntValue(value: Int) extends Addable
case class DoubleValue(value: Double) extends Addable
case class StringValue(value: String) extends Addable

// Актор для выполнения сложения
object AddingActor {
  // Сообщение, которое принимает актор: два значения и ссылка на ответный актор
  case class AddMessage(a: Addable, b: Addable, replyTo: ActorRef[Addable])

  // Определяем поведение актора
  def apply(): Behavior[AddMessage] = Behaviors.receive { (context, message) =>
    // Обработка сообщения с использованием pattern matching
    val result = (message.a, message.b) match {
      case (StringValue(a), b: Addable)     => StringValue(a + b.toString)
      case (a: Addable, StringValue(b))     => StringValue(b + a.toString)
      case (IntValue(a), IntValue(b))       => IntValue(a + b)
      case (DoubleValue(a), DoubleValue(b)) => DoubleValue(a + b)
      case (IntValue(_), DoubleValue(_))    => ???
      case (DoubleValue(_), IntValue(_))    => ???
    }

    // Отправляем результат обратно отправителю
    message.replyTo ! result
    Behaviors.same
  }
}

// Актор-клиент для генерации и отправки запросов серверу
object ClientActor {
  import AddingActor.AddMessage

  // Определяем поведение клиента
  def apply(server: ActorRef[AddMessage]): Behavior[Addable] = Behaviors.setup {
    context =>
      val random = new Random()

      // Генерация и отправка сообщений серверу с случайными числами
      server ! AddMessage(
        IntValue(random.nextInt(100)),
        IntValue(random.nextInt(100)),
        context.self
      )
      server ! AddMessage(
        DoubleValue(random.nextDouble() * 100),
        DoubleValue(random.nextDouble() * 100),
        context.self
      )
      server ! AddMessage(
        StringValue("Random number: "),
        StringValue(random.nextInt(100).toString),
        context.self
      )

      // Обработка ответа от сервера
      Behaviors.receiveMessage { result =>
        context.log.info(s"Получен результат: $result")
        Behaviors.same
      }
  }

}

// Основной объект системы для управления актерами
object AddingSystem {
  import AddingActor.AddMessage

  // Определяем поведение системы при инициализации
  def apply(): Behavior[Unit] = Behaviors.setup { context =>
    // Создаем единственный актор-сервер для сложения
    val adder = context.spawn(AddingActor(), "adder")

    // Создаем клиентский актор для взаимодействия с сервером
    context.spawn(ClientActor(adder), "client")

    // Поскольку мы не ожидаем сообщений в этом контексте, возвращаем пустое поведение
    Behaviors.empty
  }
}

// Главный метод для запуска системы
@main def AddingMain(): Unit = {
  // Запускаем ActorSystem с основным поведением
  ActorSystem(AddingSystem(), "adding-system")
}
