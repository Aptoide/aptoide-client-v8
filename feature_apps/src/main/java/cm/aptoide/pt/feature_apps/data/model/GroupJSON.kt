package cm.aptoide.pt.feature_apps.data.model

import androidx.annotation.Keep

@Keep
data class GroupJSON(
  val id: Long,
  val name: String,
  val title: String,
  val parent: GroupJSON?,
  val icon: String?,
  val graphic: String?,
  val background: String?,
)
