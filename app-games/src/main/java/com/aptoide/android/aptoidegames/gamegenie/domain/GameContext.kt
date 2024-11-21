package com.aptoide.android.aptoidegames.gamegenie.domain

import com.google.gson.annotations.SerializedName

data class GameContext(
  val name: String,
  @SerializedName("package") val packageName: String,
)
