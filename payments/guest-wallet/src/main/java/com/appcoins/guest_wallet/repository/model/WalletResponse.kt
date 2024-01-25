package com.appcoins.guest_wallet.repository.model

import androidx.annotation.Keep

@Keep
data class WalletResponse(
  val address: String?,
  val ewt: String?,
  val signature: String?,
)
