package cm.aptoide.pt.feature_editorial.data

class Article(
  val title: String,
  val subtype: ArticleType,
  val summary: String,
  val image: String,
)

enum class ArticleType {
  GAME_OF_THE_WEEK
}
