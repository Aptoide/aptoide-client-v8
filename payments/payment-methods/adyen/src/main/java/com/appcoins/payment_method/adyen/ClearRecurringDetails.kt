package com.appcoins.payment_method.adyen

import com.appcoins.payments.json.Json

@Json
data class ClearRecurringDetails(
  @Json("wallet.address") val walletAddress: String,
)
