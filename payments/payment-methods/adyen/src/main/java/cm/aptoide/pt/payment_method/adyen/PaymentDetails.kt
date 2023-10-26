package cm.aptoide.pt.payment_method.adyen

import androidx.annotation.Keep
import com.adyen.checkout.core.model.ModelObject
import com.google.gson.annotations.SerializedName

@Keep
data class PaymentDetails(
  @SerializedName("payment.method") val adyenPaymentMethod: ModelObject,
  @SerializedName("payment.store_method") val shouldStoreMethod: Boolean,
  @SerializedName("payment.return_url") val returnUrl: String,
  @SerializedName("payment.shopper_interaction") val shopperInteraction: String?,
  @SerializedName("payment.billing_address")
  val billingAddress: BillingAddress?,
  @SerializedName("callback_url") val callbackUrl: String?,
  val domain: String?,
  val metadata: String?,
  val method: String?,
  val origin: String?,
  @SerializedName("product") val sku: String?,
  val reference: String?,
  val type: String?,
  @SerializedName("price.currency") val currency: String?,
  @SerializedName("price.value") val value: String?,
  @SerializedName("wallets.developer") val developer: String?,
  @SerializedName("entity.oemid") val entityOemId: String?,
  @SerializedName("entity.domain") val entityDomain: String?,
  @SerializedName("entity.promo_code") val entityPromoCode: String?,
  @SerializedName("wallets.user") val user: String?,
  @SerializedName("referrer_url") val referrerUrl: String?,
)

@Keep
data class BillingAddress(
  val street: String,
  val city: String,
  val postalCode: String,
  val houseNumberOrName: String,
  val stateOrProvince: String,
  val country: String,
)
