import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors

object IntegralCalculator {
  case class CalculateIntegral(
      function: Double => Double,
      start: Double,
      stepSize: Double,
      totalSteps: Int,
      replyTo: ActorRef[CalculationResult]
  )
  case class CalculationResult(partialSum: Double)

  def apply(): Behavior[CalculateIntegral] = Behaviors.receive {
    (context, message) =>
      val totalSum = (0 until message.totalSteps)
        .map(i => message.start + i * message.stepSize)
        .map(x => message.function(x) * message.stepSize)
        .sum

      message.replyTo ! CalculationResult(totalSum)
      Behaviors.same
  }
}

object PartialResultAggregator {
  case class AddPartial(partialSum: Double)

  def apply(totalParts: Int, replyTo: ActorRef[Double]): Behavior[AddPartial] =
    Behaviors.setup { context =>
      def aggregate(
          currentSum: Double,
          remainingParts: Int
      ): Behavior[AddPartial] = {
        Behaviors.receiveMessage { case AddPartial(partialSum) =>
          val updatedSum = currentSum + partialSum
          context.log.info(
            s"Received partial sum: $partialSum, remaining parts: ${remainingParts - 1}"
          )

          if (remainingParts - 1 == 0) {
            replyTo ! updatedSum
            Behaviors.stopped
          } else {
            aggregate(updatedSum, remainingParts - 1)
          }
        }
      }

      aggregate(0.0, totalParts)
    }
}

object ResultLogger {
  def apply(): Behavior[Double] = Behaviors.receive { (context, result) =>
    context.log.info(s"Final calculated sum: $result")
    Behaviors.same
  }
}

object CalculationCoordinator {
  case class StartIntegralCalculation(
      function: Double => Double,
      lowerBound: Double,
      upperBound: Double,
      steps: Int
  )

  def apply(): Behavior[StartIntegralCalculation] = Behaviors.setup { context =>
    val integralCalculator =
      context.spawn(IntegralCalculator(), "integralCalculator")

    Behaviors.receiveMessage {
      case StartIntegralCalculation(function, lowerBound, upperBound, steps) =>
        val stepSize = (upperBound - lowerBound) / steps
        val numberOfActors = 4

        val resultLogger = context.spawn(ResultLogger(), "resultLogger")
        val aggregator = context.spawn(
          PartialResultAggregator(numberOfActors, resultLogger),
          "partialAggregator"
        )

        def initiateCalculation(
            start: Double,
            steps: Int,
            replyTo: ActorRef[IntegralCalculator.CalculationResult]
        ): Unit =
          integralCalculator ! IntegralCalculator.CalculateIntegral(
            function,
            start,
            stepSize,
            steps,
            replyTo
          )

        (0 until numberOfActors).foreach { i =>
          val start = lowerBound + i * (steps / numberOfActors) * stepSize
          val responseActor = context.spawn(
            Behaviors.receiveMessage[IntegralCalculator.CalculationResult] {
              case IntegralCalculator.CalculationResult(partialSum) =>
                aggregator ! PartialResultAggregator.AddPartial(partialSum)
                Behaviors.same
            },
            s"responseActor-$i"
          )

          initiateCalculation(start, steps / numberOfActors, responseActor)
        }

        Behaviors.same
    }
  }
}

@main def runIntegralCalculation(): Unit = {
  val system =
    ActorSystem(CalculationCoordinator(), "integralCalculationSystem")

  system ! CalculationCoordinator.StartIntegralCalculation(
    x => math.sqrt(x * x * x + 10),
    0,
    25,
    250
  )
}
