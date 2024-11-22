package com.aptoide.android.aptoidegames.gamegenie.domain

import androidx.annotation.Keep
import com.aptoide.android.aptoidegames.gamegenie.io_models.TokenResponse

@Keep
data class Token(
  val token: String,
  val tokenType: String,
)

fun TokenResponse.toToken() = Token(this.accessToken, this.tokenType)
