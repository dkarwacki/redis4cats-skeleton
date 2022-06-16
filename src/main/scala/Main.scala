import cats.effect.{ExitCode, IO, IOApp, Resource}
import config.Configuration
import dev.profunktor.redis4cats.{Redis, RedisCommands}
import dev.profunktor.redis4cats.effect.Log.Stdout.instance
import io.circe.Decoder.decodeString
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import redis.{RedisCacheImpl, RedisKey}

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    lazy val config = ConfigSource.default.loadOrThrow[Configuration]
    lazy val redisCommandsResource
        : Resource[IO, RedisCommands[IO, String, String]] = Redis[IO].utf8(
      s"redis://${config.redis.password.value}@${config.redis.host}:${config.redis.port}/${config.redis.database}"
    )

    lazy val program = redisCommandsResource.use { redisCommands =>
      lazy val redisCache: RedisCacheImpl = new RedisCacheImpl(redisCommands)

      lazy val fetchResult =
        IO.println("fetching result!") *> IO.pure("result string")

      lazy val fetchCachedResult =
        redisCache.cached(
          io = fetchResult,
          key = RedisKey("key1"),
          ttl = 5 seconds
        )

      for {
        //fetch initial result
        result <- fetchCachedResult
        _ <- IO.println(s"initial result - $result")
        _ <- IO.sleep(2 seconds)

        //fetch result from cache
        result2 <- fetchCachedResult
        _ <- IO.println(s"result from cache - $result2")
        _ <- IO.sleep(4 seconds)

        //fetch result again as cache expired
        result3 <- fetchCachedResult
        _ <- IO.println(s"result - $result3")
      } yield ()
    }

    program.as(ExitCode.Success)
  }
}
