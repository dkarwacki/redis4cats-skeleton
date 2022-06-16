package redis

import cats.effect.IO
import io.circe.{Decoder, Encoder}

import scala.concurrent.duration.FiniteDuration

trait RedisCache {
  def cached[T](io: IO[T], key: RedisKey, ttl: FiniteDuration)(implicit
      decoder: Decoder[T],
      encoder: Encoder[T]
  ): IO[T]
}
