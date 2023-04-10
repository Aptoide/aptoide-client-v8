package cm.aptoide.pt.feature_categories.domain

data class Category(
  val id: Long,
  val name: String,
  val title: String,
  val icon: String? = null,
  val graphic: String? = null,
  val background: String? = null
)
