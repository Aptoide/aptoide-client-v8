package cm.aptoide.pt.feature_editorial.data

import cm.aptoide.pt.feature_editorial.data.network.ContentJSON

data class ArticleDetail(
  val title: String,
  val subtype: ArticleType,
  val image: String,
  val date: String,
  val views: Long,
  val content: List<ContentJSON>,
)
