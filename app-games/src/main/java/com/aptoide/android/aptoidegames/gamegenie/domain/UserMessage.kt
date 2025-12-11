package com.aptoide.android.aptoidegames.gamegenie.domain

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class UserMessage(
  @SerializedName("text") val text: String,
  @SerializedName("image") val image: String? = null,
)
