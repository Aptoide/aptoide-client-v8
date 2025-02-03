package cm.aptoide.pt.appcomingsoon.repository

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AppComingSoonCardJson(
  val id: String,
  val type: String,
  val subType: String,
  val title: String,
  val slug: String,
  val caption: String,
  val summary: String,
  val icon: String,
  val url: String,
  val views: Int,
  val appearance: Appearance,
  val date: String,
  val graphic: String,
  @SerializedName(value = "package") var packageName: String,
)

@Keep
data class Appearance(val caption: Caption)

@Keep
data class Caption(val theme: String)
