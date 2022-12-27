package cm.aptoide.pt.feature_editorial.data

class Article(
  val id: String,
  val title: String,
  val caption: String,
  val subtype: ArticleType,
  val summary: String,
  val image: String,
  val date: String,
  val views: Long,
)

enum class ArticleType {
  APP_OF_THE_WEEK,
  COLLECTION,
  GAME_OF_THE_WEEK,
  NEW_APP,
  NEWS,
  OTHER
}
