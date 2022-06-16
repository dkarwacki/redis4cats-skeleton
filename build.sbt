name := "fruits"
scalaVersion := "2.13.8"

lazy val catsVersion = "2.7.0"
lazy val catsEffectVersion = "3.3.5"
lazy val scalaTestVersion = "3.2.11"
lazy val pureConfigVersion = "0.17.1"
lazy val logbackVersion = "1.2.10"
lazy val log4CatsVersion = "2.2.0"
lazy val circeVersion = "0.14.1"
lazy val newTypeVersion = "0.4.4"
lazy val redis4CatsVersion = "1.1.1"
lazy val embeddedRedisVersion = "0.4.0"

lazy val catsDependencies = Seq(
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.typelevel" %% "cats-effect" % catsEffectVersion,
  "org.typelevel" %% "cats-effect-kernel" % catsEffectVersion,
  "org.typelevel" %% "cats-effect-std" % catsEffectVersion,
  "org.typelevel" %% "cats-effect-laws" % catsEffectVersion % Test
)

lazy val scalaTestDependencies = Seq(
  "org.scalactic" %% "scalactic" % scalaTestVersion % Test,
  "org.scalatest" %% "scalatest" % scalaTestVersion % Test
)

lazy val pureConfigDependencies = Seq(
  "com.github.pureconfig" %% "pureconfig" % pureConfigVersion
)

lazy val loggerDependencies = Seq(
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "ch.qos.logback" % "logback-core" % logbackVersion,
  "org.typelevel" %% "log4cats-slf4j" % log4CatsVersion
)

lazy val circeDependencies = Seq(
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "io.circe" %% "circe-literal" % circeVersion,
  "io.circe" %% "circe-refined" % circeVersion,
  "io.circe" %% "circe-generic-extras" % circeVersion
)

lazy val newTypeDependencies = Seq(
  "io.estatico" %% "newtype" % newTypeVersion
)

lazy val redisDependencies = Seq(
  "dev.profunktor" %% "redis4cats-effects" % redis4CatsVersion
)

lazy val embeddedRedisDependencies = Seq(
  "com.github.sebruck" %% "scalatest-embedded-redis" % embeddedRedisVersion
)

lazy val root = (project in file("."))
  .settings(
    scalacOptions ++= Seq(
      "-Ymacro-annotations"
    ),
    libraryDependencies ++=
      catsDependencies
        ++ scalaTestDependencies
        ++ pureConfigDependencies
        ++ loggerDependencies
        ++ circeDependencies
        ++ newTypeDependencies
        ++ redisDependencies
        ++ embeddedRedisDependencies
  )
