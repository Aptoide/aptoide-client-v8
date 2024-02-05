package com.appcoins.payment_method.adyen

import com.adyen.checkout.components.model.payments.request.PaymentMethodDetails
import com.appcoins.payment_method.adyen.repository.AdyenV2Repository
import com.appcoins.payments.arch.PaymentMethod
import com.appcoins.payments.arch.ProductInfoData
import com.appcoins.payments.arch.PurchaseRequest
import com.appcoins.payments.arch.WalletData
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

  override suspend fun createTransaction(
    paymentDetails: Pair<String, PaymentMethodDetails>,
    storePaymentMethod: Boolean,
  ): CreditCardTransaction =
    adyenRepository.createTransaction(
      ewt = wallet.ewt,
      walletAddress = wallet.address,
      paymentDetails = PaymentDetails(
        adyenPaymentMethod = paymentDetails.second,
        shouldStoreMethod = storePaymentMethod,
        returnUrl = paymentDetails.first,
        shopperInteraction = "Ecommerce",
        billingAddress = null,
        callbackUrl = purchaseRequest.callbackUrl,
        domain = purchaseRequest.domain,
        metadata = purchaseRequest.metadata,
        method = "credit_card",
        origin = purchaseRequest.origin,
        sku = productInfo.sku,
        reference = purchaseRequest.orderReference,
        type = purchaseRequest.type,
        currency = productInfo.priceCurrency,
        value = productInfo.priceValue,
        developer = developerWallet,
        entityOemId = purchaseRequest.oemId,
        entityDomain = purchaseRequest.oemPackage,
        entityPromoCode = null,
        user = wallet.address,
        referrerUrl = purchaseRequest.uri.toString()
      )
    ).let {
      CreditCardTransaction(
        initialStatus = it.status,
        walletData = wallet,
        adyenRepository = adyenRepository,
        _paymentResponse = it.payment,
        uid = it.uid
      )
    }

  suspend fun clearStoredCard() = adyenRepository.clearStoredCard(wallet.address)
}
