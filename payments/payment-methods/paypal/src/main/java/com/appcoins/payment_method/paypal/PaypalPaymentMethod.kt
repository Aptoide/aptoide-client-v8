package com.appcoins.payment_method.paypal

import com.appcoins.payment_method.paypal.model.PaypalTransaction
import com.appcoins.payment_method.paypal.repository.PaypalRepository
import com.appcoins.payment_method.paypal.repository.model.PaymentDetailsRequest
import com.appcoins.payments.arch.PaymentMethod
import com.appcoins.payments.arch.ProductInfoData
import com.appcoins.payments.arch.PurchaseRequest
import com.appcoins.payments.arch.WalletData
import kotlinx.coroutines.flow.flowOf

class PaypalPaymentMethod internal constructor(
  override val id: String,
  override val label: String,
  override val iconUrl: String,
  override val available: Boolean,
  override val developerWallet: String,
  override val wallet: WalletData,
  override val productInfo: ProductInfoData,
  override val purchaseRequest: PurchaseRequest,
  private val paypalRepository: PaypalRepository,
) : PaymentMethod<Unit> {

  private val successSchema = Constants.PAYPAL_SUCCESS_SCHEMA
  private val cancelSchema = Constants.PAYPAL_ERROR_SCHEMA

  suspend fun init() = try {
    paypalRepository.getCurrentBillingAgreement(
      ewt = wallet.ewt,
      walletAddress = wallet.address
    )
  } catch (e: Exception) {
    null
  }

  override suspend fun createTransaction(
    paymentDetails: Unit,
    storePaymentMethod: Boolean,
  ): PaypalTransaction =
    paypalRepository.createTransaction(
      ewt = wallet.ewt,
      walletAddress = wallet.address,
      paymentDetails = PaymentDetailsRequest(
        callbackUrl = purchaseRequest.callbackUrl,
        domain = purchaseRequest.domain,
        metadata = purchaseRequest.metadata,
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
        referrerUrl = purchaseRequest.uri.toString(),
      )
    ).let {
      PaypalTransaction(
        uid = it.uid,
        status = flowOf(it.status),
      )
    }

  suspend fun createToken(packageName: String) =
    paypalRepository.createToken(
      ewt = wallet.ewt,
      walletAddress = wallet.address,
      cancelUrl = "$cancelSchema://$packageName",
      returnUrl = "$successSchema://$packageName"
    )

  suspend fun cancelToken(token: String) = paypalRepository.cancelToken(
    ewt = wallet.ewt,
    walletAddress = wallet.address,
    token = token
  )

  suspend fun createBillingAgreement(token: String) =
    paypalRepository.createBillingAgreement(
      ewt = wallet.ewt,
      walletAddress = wallet.address,
      token = token,
    )

  suspend fun cancelBillingAgreement() =
    paypalRepository.removeBillingAgreement(
      ewt = wallet.ewt,
      walletAddress = wallet.address,
    )
}
