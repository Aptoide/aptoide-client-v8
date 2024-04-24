package com.appcoins.payments.methods.adyen

import com.appcoins.payments.json.Json

@Json
data class ClearRecurringDetails(
  @Json("wallet.address") val walletAddress: String,
)
