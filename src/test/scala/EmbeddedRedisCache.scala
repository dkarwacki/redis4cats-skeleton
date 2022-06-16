import cats.effect.IO
import cats.effect.unsafe.IORuntime
import com.github.sebruck.EmbeddedRedis
import dev.profunktor.redis4cats.Redis
import dev.profunktor.redis4cats.effect.Log.NoOp.instance
import org.scalatest.{BeforeAndAfterAll, Suite}
import redis.embedded.RedisServer
import redis.{RedisCache, RedisCacheImpl}

trait EmbeddedRedisCache extends EmbeddedRedis with BeforeAndAfterAll {
  self: Suite =>
  private var stopRedisCacheResource: IO[Unit] = _
  private var redisServer: RedisServer = _
  var redisCache: RedisCache = _

  override protected def beforeAll(): Unit = {
    implicit val ioRuntime: IORuntime = IORuntime.global
    redisServer = startRedis()
    val (redisCommands, stopRedisResource) = Redis[IO]
      .utf8(s"redis://localhost:${redisServer.ports().get(0)}")
      .allocated
      .unsafeRunSync()
    stopRedisCacheResource = stopRedisResource
    redisCache = new RedisCacheImpl(redisCommands)
    super.beforeAll()
  }

  override protected def afterAll(): Unit = {
    stopRedis(redisServer)
    super.afterAll()
  }
}
