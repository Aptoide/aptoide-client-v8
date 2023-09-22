package cm.aptoide.pt.feature_payment.repository.broker.model

import androidx.annotation.Keep
import java.math.BigDecimal

@Keep
data class PaymentMethodsResponse(
  val next: String?,
  val items: List<PaymentMethodJSON>
)

@Keep
data class PaymentMethodJSON(
  val name: String,
  val label: String,
  val icon: String,
  val status: String,
  val message: String?,
  val gateway: GatewayJSON,
  val async: Boolean,
  val fee: FeeJSON?,
) {
  val isAvailable: Boolean
    get() = this.status != "UNAVAILABLE"
}

@Keep
data class GatewayJSON(
  val name: String
)

@Keep
data class FeeJSON(
  val type: FeeType,
  val cost: FeeCostJSON?,
)

@Keep
enum class FeeType {
  EXACT,
  UNKNOWN
}

@Keep
data class FeeCostJSON(
  val value: BigDecimal,
  val currency: String,
)
