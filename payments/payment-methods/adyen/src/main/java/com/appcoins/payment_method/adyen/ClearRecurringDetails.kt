package com.appcoins.payment_method.adyen

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ClearRecurringDetails(
  @SerializedName("wallet.address") val walletAddress: String
)
