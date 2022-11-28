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
  APP_OF_THE_WEEK("App of The Week"),
  COLLECTION("Collection"),
  GAME_OF_THE_WEEK("Game of The Week"),
  NEW_APP("New App"),
  NEWS("News"),
  OTHER("Other")
}
