package cm.aptoide.pt.feature_categories.domain

import cm.aptoide.pt.extensions.getRandomString

data class Category(
  val id: Long,
  val name: String,
  val title: String,
  val icon: String? = null,
  val graphic: String? = null,
  val background: String? = null,
)

val randomCategory
  get() = Category(
    id = System.currentTimeMillis(),
    name = getRandomString(range = 1..1),
    title = getRandomString(range = 1..3, capitalize = true),
  )
