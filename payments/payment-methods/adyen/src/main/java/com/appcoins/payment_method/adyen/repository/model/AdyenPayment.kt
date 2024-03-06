package com.appcoins.payment_method.adyen.repository.model

import androidx.annotation.Keep
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

@Keep
data class AdyenPayment(
  @SerializedName("payment.details") val details: JsonObject,
  @SerializedName("payment.data") val data: String?,
)
