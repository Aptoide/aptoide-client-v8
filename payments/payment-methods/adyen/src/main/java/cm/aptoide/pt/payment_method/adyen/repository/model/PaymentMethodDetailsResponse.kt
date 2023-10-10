package cm.aptoide.pt.payment_method.adyen.repository.model

import androidx.annotation.Keep
import com.google.gson.JsonObject
import java.math.BigDecimal

@Keep
data class PaymentMethodDetailsResponse(
  val price: AdyenPrice,
  val payment: JsonObject,
)

@Keep
data class AdyenPrice(
  val value: BigDecimal,
  val currency: String,
)
