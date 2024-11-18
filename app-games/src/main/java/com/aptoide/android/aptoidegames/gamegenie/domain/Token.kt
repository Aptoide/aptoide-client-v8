package com.aptoide.android.aptoidegames.gamegenie.domain

import com.aptoide.android.aptoidegames.gamegenie.io_models.TokenResponse

data class Token(
    val token: String,
    val tokenType: String
)

fun TokenResponse.toToken() = Token(this.accessToken, this.tokenType)