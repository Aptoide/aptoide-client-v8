package cm.aptoide.pt.feature_editorial.data.network.model

import androidx.annotation.Keep

@Keep
data class EditorialJson(
  val id: String,
  val type: String,
  val subtype: String,
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

@Keep
data class Appearance(val caption: Caption)

@Keep
data class Caption(val theme: String)
