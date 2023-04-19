package cm.aptoide.pt.feature_editorial.data.model

import androidx.annotation.Keep

@Keep
data class EditorialJson(
  val card_id: String,
  val layout: String,
  val subtype: String,
  val flair: String?,
  val title: String,
  val slug: String,
  val message: String,
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
