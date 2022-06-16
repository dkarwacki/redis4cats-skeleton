import pureconfig.{ConfigCursor, ConfigReader}

package object config {
  case class SensitiveString(value: String) extends AnyVal {
    override def toString: String = "***"
  }

  object SensitiveString {
    implicit val configReader: ConfigReader[SensitiveString] =
      (cur: ConfigCursor) => cur.asString.map(SensitiveString.apply)
  }
}
