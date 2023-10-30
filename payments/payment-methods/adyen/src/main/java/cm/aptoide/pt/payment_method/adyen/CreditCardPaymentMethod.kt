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
  override val developerWallet: String,
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
        returnUrl = paymentDetails.first,
        shopperInteraction = "Ecommerce",
        billingAddress = null,
        callbackUrl = purchaseRequest.callbackUrl,
        domain = purchaseRequest.domain,
        metadata = purchaseRequest.metadata,
        method = "credit_card",
        origin = "BDS",
        sku = productInfo.sku,
        reference = purchaseRequest.orderReference,
        type = "INAPP_UNMANAGED",
        currency = productInfo.priceCurrency,
        value = productInfo.priceValue,
        developer = developerWallet,
        entityOemId = purchaseRequest.oemId,
        entityDomain = purchaseRequest.oemPackage,
        entityPromoCode = null,
        user = wallet.address,
        referrerUrl = purchaseRequest.ospUri.toString()
      )
    ).let {
      CreditCardTransaction(
        initialStatus = it.status,
        walletData = wallet,
        adyenRepository = adyenRepository,
      )
    }
}
