package cm.aptoide.pt.payment_method.adyen.repository.model

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class AdyenPayment(
  @SerializedName("payment.details") val details: JsonObject,
  @SerializedName("payment.data") val data: String?
)
