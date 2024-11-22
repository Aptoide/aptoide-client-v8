package com.aptoide.android.aptoidegames.gamegenie.io_models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class TokenResponse(
  @SerializedName("access_token")
  val accessToken: String,
  @SerializedName("token_type")
  val tokenType: String,
)