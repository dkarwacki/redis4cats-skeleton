import io.estatico.newtype.macros.newtype

package object redis {
  @newtype case class RedisKey(value: String)
}
