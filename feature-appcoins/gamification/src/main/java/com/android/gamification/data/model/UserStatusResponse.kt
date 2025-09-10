package com.android.gamification.data.model

import com.google.gson.annotations.SerializedName

data class UserStatusResponse(
  val promotions: List<GamificationResponse>,
  @SerializedName("wallet_origin") val walletOrigin: WalletOrigin
)

enum class WalletOrigin {
  UNKNOWN, APTOIDE, PARTNER, PARTNER_NO_BONUS
}
