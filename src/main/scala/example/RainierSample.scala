package example

import cats.effect.Sync
import com.stripe.rainier.core.{Generator, LogNormal, Poisson, Predictor, RandomVariable}

trait RainierSample {

  def doRegression[F[_]: Sync](data: List[(Int, Int)], value: Int): F[RandomVariable[Generator[Int]]] = Sync[F].delay {
    for {
      slope <- LogNormal(0, 1).param
      intercept <- LogNormal(0, 1).param
      regression <- Predictor[Int].from { x => Poisson(x * slope + intercept) }.fit(data)
    } yield regression.predict(value)
  }

}
