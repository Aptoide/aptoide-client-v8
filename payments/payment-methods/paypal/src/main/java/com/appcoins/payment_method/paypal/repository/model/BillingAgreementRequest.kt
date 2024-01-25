package com.appcoins.payment_method.paypal.repository.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class BillingAgreementRequest(
  @SerializedName("urls") val urls: Urls,
)

@Keep
internal data class Urls(
  @SerializedName("return") val returnUrl: String,
  @SerializedName("cancel") val cancelUrl: String,
)
