package cm.aptoide.pt.feature_appview.domain.model


data class RelatedCard(
  val id: String,
  val type: String,
  val subType: String,
  val flair: String?,
  val title: String,
  val slug: String,
  val caption: String,
  val summary: String,
  val icon: String,
  val url: String,
  val views: Long,
  val appearance: Appearance,
  val date: String,
)

data class Appearance(val caption: Caption)
data class Caption(val theme: String)