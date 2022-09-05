package cm.aptoide.pt.feature_editorial.data

class Article(
  val id: String,
  val title: String,
  val subtype: ArticleType,
  val summary: String,
  val image: String,
  val date: String,
  val views: Long,
)

enum class ArticleType(val label: String) {
  GAME_OF_THE_WEEK("Game of The Week");
}
