package cm.aptoide.pt.payment_method.adyen

import cm.aptoide.pt.payment_manager.manager.PurchaseRequest
import cm.aptoide.pt.payment_manager.payment.PaymentMethod
import cm.aptoide.pt.payment_manager.repository.product.domain.ProductInfoData
import cm.aptoide.pt.payment_manager.transaction.Transaction
import cm.aptoide.pt.payment_manager.wallet.WalletData
import cm.aptoide.pt.payment_method.adyen.repository.AdyenV2Repository
import com.adyen.checkout.components.model.payments.request.PaymentMethodDetails
import org.json.JSONObject

class CreditCardPaymentMethod internal constructor(
  override val id: String,
  override val label: String,
  override val iconUrl: String,
  override val available: Boolean,
  override val wallet: WalletData,
  override val productInfo: ProductInfoData,
  override val purchaseRequest: PurchaseRequest,
  private val adyenRepository: AdyenV2Repository,
) : PaymentMethod<Pair<String, PaymentMethodDetails>> {

  suspend fun init(): JSONObject {
    val paymentMethodDetails = adyenRepository.getPaymentMethodDetails(
      ewt = wallet.ewt,
      walletAddress = wallet.address,
      priceValue = productInfo.priceValue,
      priceCurrency = productInfo.priceCurrency
    )

    return paymentMethodDetails.json
  }

  override suspend fun createTransaction(paymentDetails: Pair<String, PaymentMethodDetails>): Transaction =
    adyenRepository.createTransaction(
      ewt = wallet.ewt,
      walletAddress = wallet.address,
      paymentDetails = PaymentDetails(
        adyenPaymentMethod = paymentDetails.second,
        shouldStoreMethod = false,
        returnUrl = "gh://${paymentDetails.first}",
        shopperInteraction = null, //Ecommerce by default, according to docs
        billingAddress = null,
        callbackUrl = purchaseRequest.callbackUrl, //from PurchaseRequest
        domain = purchaseRequest.domain, //from PurchaseRequest. It should be the packageName
        metadata = null,
        method = "credit_card",
        origin = "BDS", //what should it be??? "GH"?
        sku = productInfo.sku,
        reference = purchaseRequest.orderReference, //from PurchaseRequest
        type = "INAPP_UNMANAGED", //what should it be??,
        currency = productInfo.priceCurrency,
        value = productInfo.priceValue,
        developer = null, //how to get developer wallet address?
        entityOemId = purchaseRequest.oemId, //from PurchaseRequest,
        entityDomain = null, //from PurchaseRequest. How is it diff from domain param?
        entityPromoCode = null,
        user = wallet.address, //user wallet address
        referrerUrl = purchaseRequest.ospUri.toString() //what is this?
      )
    ).let {
      CreditCardTransaction(
        initialStatus = it.status,
        walletData = wallet,
        adyenRepository = adyenRepository,
      )
    }
}
