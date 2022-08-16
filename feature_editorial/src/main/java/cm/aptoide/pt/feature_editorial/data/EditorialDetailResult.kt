package cm.aptoide.pt.feature_editorial.data

import cm.aptoide.pt.feature_editorial.data.network.App
import cm.aptoide.pt.feature_editorial.data.network.Media

data class ArticleDetail(
  val title: String,
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
