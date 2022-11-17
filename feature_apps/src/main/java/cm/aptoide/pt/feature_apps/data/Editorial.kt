package cm.aptoide.pt.feature_apps.data

import cm.aptoide.pt.feature_editorial.data.ArticleType

data class Editorial(
  val id: String,
  val title: String,
  val summary: String,
  val image: String,
  val subtype: ArticleType,
  val date: String,
  val views: Long,
  val reactionsNumber: Int,
)
