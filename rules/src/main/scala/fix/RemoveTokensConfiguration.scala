package fix


import metaconfig.ConfDecoder
import metaconfig.ConfEncoder
import metaconfig.annotation.Description
import metaconfig.generic

case class FilenameGlob(
    @Description("Glob pattern to match files")
    pattern: String = "**/*.scala",
    @Description("Optional path to directory to search from")
    baseDirectory: Option[String] = None
)
object FilenameGlob {
  implicit val surface = generic.deriveSurface[FilenameGlob]
  implicit val decoder = generic.deriveDecoder[FilenameGlob](FilenameGlob())
  implicit val encoder = generic.deriveEncoder[FilenameGlob]
}

case class UnwantedTokens(
    @Description("List of tokens to remove")
    tokens: Set[String]
)

object UnwantedTokens {
  implicit val surface = generic.deriveSurface[UnwantedTokens]
  implicit val decoder = generic.deriveDecoder[UnwantedTokens](UnwantedTokens(Set()))
  implicit val encoder = generic.deriveEncoder[UnwantedTokens]
}

case class RemoveTokensConfiguration(
    @Description("Tokens to remove from files")
    tokens: Map[FilenameGlob, UnwantedTokens] = Map()
)

object RemoveTokensConfiguration {
  def default = RemoveTokensConfiguration()
  implicit val surface =
    metaconfig.generic.deriveSurface[RemoveTokensConfiguration]
  implicit val decoder =
    metaconfig.generic.deriveDecoder(default)
}
