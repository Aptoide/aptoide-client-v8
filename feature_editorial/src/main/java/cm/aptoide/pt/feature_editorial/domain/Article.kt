package cm.aptoide.pt.feature_editorial.domain

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_editorial.data.model.Media

data class Article(
  val id: String,
  val title: String,
  val caption: String,
  val subtype: ArticleType,
  val image: String,
  val date: String,
  val views: Long,
  val content: List<Paragraph>,
)

data class Paragraph(
  val title: String?,
  val message: String?,
  val action: Action?,
  val media: List<Media>,
  val app: App?,
)

data class ArticleMeta(
  val id: String,
  val title: String,
  val url: String,
  val caption: String,
  val summary: String,
  val image: String,
  val subtype: ArticleType,
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

data class Action(val title: String, val url: String)
