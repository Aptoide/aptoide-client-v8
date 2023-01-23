package cm.aptoide.pt.feature_editorial.domain

import cm.aptoide.pt.feature_editorial.data.ArticleType

data class EditorialMeta(
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
