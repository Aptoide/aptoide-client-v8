package com.aptoide.android.aptoidegames.promotions.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class PromotionJson(
  @SerializedName("app_name") val packageName: String,
  val aliases: Map<String, String>,
  val image: String,
  val title: String,
  val content: String,
  val uri: String,
  val metadata: MetadataJson?,
  val uid: String,
)

@Keep
data class MetadataJson(
  @SerializedName("user_bonus") val userBonus: Int,
)
