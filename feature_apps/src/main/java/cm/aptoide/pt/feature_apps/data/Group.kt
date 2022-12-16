package cm.aptoide.pt.feature_apps.data

import androidx.annotation.Keep

@Keep
data class Group(
  val id: Long,
  val name: String,
  val title: String,
  val parent: Group?,
  val icon: String?,
  val graphic: String?,
  val background: String?,
)