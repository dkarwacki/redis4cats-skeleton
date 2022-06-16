import cats.effect.IO
import cats.effect.unsafe.implicits.global
import io.circe.generic.auto._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import redis.RedisKey

import java.util.UUID
import scala.language.postfixOps

class RedisCacheImplTest
    extends AnyFlatSpec
    with EmbeddedRedisCache
    with Matchers {

  case class TestValue(value: String)
  class RandomTestValueProducer {
    def produce(): IO[TestValue] = IO(TestValue(UUID.randomUUID().toString))
  }

  "RedisCache" should "cache given test value for specified (ttl) time" in {
    // given
    val testKey = RedisKey("key")

    val randomTestValueProducer = new RandomTestValueProducer()

    val fetchCachedValue: IO[TestValue] = redisCache
      .cached[TestValue](
        io = randomTestValueProducer.produce(),
        key = testKey,
        ttl = 1 second
      )

    // when
    val result = (
      for {
        //fetch result1 and set it to cache
        result1 <- fetchCachedValue

        //fetch cached result1
        cachedResult1 <- fetchCachedValue

        _ <- IO.sleep(2 seconds)

        //fetch result2 as cache with result1 expired
        result2 <- fetchCachedValue

        //fetch cached result2
        cachedResult2 <- fetchCachedValue

      } yield result1 == cachedResult1 && result2 == cachedResult2 && result1 != result2
    )
      .unsafeRunSync()

    // then
    result shouldEqual true
  }
}
