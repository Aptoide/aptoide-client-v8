package com.aptoide.android.aptoidegames.gamegenie.domain

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class GameContext(
  val name: String,
  @SerializedName("package") val packageName: String,
)
