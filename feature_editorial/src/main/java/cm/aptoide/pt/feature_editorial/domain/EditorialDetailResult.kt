package cm.aptoide.pt.feature_editorial.domain

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_editorial.data.ArticleType
import cm.aptoide.pt.feature_editorial.data.network.Media

data class ArticleDetail(
  val id: String,
  val title: String,
  val caption: String,
  val subtype: ArticleType,
  val image: String,
  val date: String,
  val views: Long,
  val content: List<ArticleContent>,
)

data class ArticleContent(
  val title: String?,
  val message: String?,
  val action: String?,
  val media: List<Media>,
  val app: App?,
)
