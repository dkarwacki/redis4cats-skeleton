package redis

import cats.effect.IO
import cats.implicits.toTraverseOps
import dev.profunktor.redis4cats.RedisCommands
import dev.profunktor.redis4cats.effects.SetArg.Ttl.Px
import dev.profunktor.redis4cats.effects.SetArgs
import io.circe.{parser, Decoder, Encoder}
import io.circe.syntax.EncoderOps

import scala.concurrent.duration.FiniteDuration

class RedisCacheImpl(redisCommands: RedisCommands[IO, String, String])
    extends RedisCache {
  override def cached[T](io: IO[T], key: RedisKey, ttl: FiniteDuration)(implicit
      decoder: Decoder[T],
      encoder: Encoder[T]
  ): IO[T] = get(key).flatMap {
    case Some(value) => IO.pure(value)
    case None =>
      io.flatMap(result =>
        set(key = key, value = result, ttl = ttl) *> IO.pure(result)
      )
  }

  private def set[T](key: RedisKey, value: T, ttl: FiniteDuration)(implicit
      encoder: Encoder[T]
  ): IO[Unit] =
    redisCommands
      .setEx(
        key = key.value,
        value = value.asJson.noSpaces,
        expiresIn = ttl
      )
      .void

  private def get[T](
      key: RedisKey
  )(implicit decoder: Decoder[T]): IO[Option[T]] =
    redisCommands
      .get(key = key.value)
      .flatMap(jsonStringOpt =>
        jsonStringOpt.traverse(jsonString =>
          IO.fromTry(parser.parse(jsonString).toTry.flatMap(_.as[T].toTry))
        )
      )
}
