package config

case class RedisConfiguration(
    host: String,
    port: Int,
    password: SensitiveString,
    database: Int
)
