package com.appcoins.payment_method.paypal.repository

import com.appcoins.payment_method.paypal.model.BillingAgreement
import com.appcoins.payment_method.paypal.model.TokenData
import com.appcoins.payment_method.paypal.repository.model.BillingAgreementRequest
import com.appcoins.payment_method.paypal.repository.model.BillingAgreementResponse
import com.appcoins.payment_method.paypal.repository.model.PaymentDetailsRequest
import com.appcoins.payment_method.paypal.repository.model.TokenResponse
import com.appcoins.payment_method.paypal.repository.model.TransactionError
import com.appcoins.payment_method.paypal.repository.model.TransactionResponse
import com.appcoins.payment_method.paypal.repository.model.Urls
import com.appcoins.payments.network.HttpException
import com.appcoins.payments.network.RestClient
import com.appcoins.payments.network.get
import com.appcoins.payments.network.post
import java.time.Duration
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class PaypalRepositoryImpl @Inject constructor(
  private val restClient: RestClient,
  private val paypalHttpHeaderProvider: PaypalHttpHeadersProvider,
) : PaypalRepository {

  override suspend fun createTransaction(
    ewt: String,
    walletAddress: String,
    paymentDetails: PaymentDetailsRequest,
  ): TransactionResponse =
    try {
      restClient.post<TransactionResponse>(
        path = "broker/8.20230522/gateways/paypal/transactions",
        header = mapOf(
          "PayPal-Client-Metadata-Id" to paypalHttpHeaderProvider.getMetadataId(),
          "authorization" to "Bearer $ewt"
        ),
        query = mapOf("wallet.address" to walletAddress),
        body = paymentDetails,
        timeout = Duration.ofSeconds(30),
      )
    } catch (e: HttpException) {
      throw handleHttpException(e)
    }

  private fun handleHttpException(httpException: HttpException): Throwable {
    if (httpException.code == 404) {
      val transactionError = httpException.parseBodyTo<TransactionError>()
      if (transactionError?.code == "Paypal.BillingAgreement.NotFound") {
        return NoBillingAgreementException()
      }
    }
    return httpException
  }

  override suspend fun createToken(
    ewt: String,
    walletAddress: String,
    cancelUrl: String,
    returnUrl: String,
  ): TokenData {
    val token = restClient.post<TokenResponse>(
      path = "broker/8.20230522/gateways/paypal/billing-agreement/token/create",
      header = mapOf(
        "PayPal-Client-Metadata-Id" to paypalHttpHeaderProvider.getMetadataId(),
        "authorization" to "Bearer $ewt"
      ),
      query = mapOf("wallet.address" to walletAddress),
      body = BillingAgreementRequest(
        urls = Urls(
          returnUrl = returnUrl,
          cancelUrl = cancelUrl
        )
      ),
      timeout = Duration.ofSeconds(30),
    )

    return TokenData(
      token = token.token,
      url = token.redirect.url
    )
  }

  override suspend fun cancelToken(
    ewt: String,
    walletAddress: String,
    token: String,
  ): Boolean = restClient.post<Unit>(
    path = "broker/8.20230522/gateways/paypal/billing-agreement/token/cancel",
    header = mapOf(
      "PayPal-Client-Metadata-Id" to paypalHttpHeaderProvider.getMetadataId(),
      "authorization" to "Bearer $ewt"
    ),
    query = mapOf("wallet.address" to walletAddress),
    body = token,
    timeout = Duration.ofSeconds(30),
  ).let { true }

  override suspend fun createBillingAgreement(
    ewt: String,
    walletAddress: String,
    token: String,
  ): BillingAgreement {
    val billingAgreementResponse = restClient.post<BillingAgreementResponse>(
      path = "broker/8.20230522/gateways/paypal/billing-agreement/create",
      header = mapOf(
        "PayPal-Client-Metadata-Id" to paypalHttpHeaderProvider.getMetadataId(),
        "authorization" to "Bearer $ewt"
      ),
      query = mapOf("wallet.address" to walletAddress),
      body = token,
      timeout = Duration.ofSeconds(30),
    )
    return BillingAgreement(uid = billingAgreementResponse.uid)
  }

  override suspend fun getCurrentBillingAgreement(
    ewt: String,
    walletAddress: String,
  ): BillingAgreement {
    val response = restClient.get<BillingAgreementResponse>(
      path = "broker/8.20230522/gateways/paypal/billing-agreement",
      header = mapOf(
        "PayPal-Client-Metadata-Id" to paypalHttpHeaderProvider.getMetadataId(),
        "authorization" to "Bearer $ewt"
      ),
      query = mapOf("wallet.address" to walletAddress),
      timeout = Duration.ofSeconds(30),
    )
    return BillingAgreement(uid = response.uid)
  }

  override suspend fun removeBillingAgreement(
    ewt: String,
    walletAddress: String,
  ): Boolean = restClient.post<Unit>(
    path = "broker/8.20230522/gateways/paypal/billing-agreement/cancel",
    header = mapOf(
      "PayPal-Client-Metadata-Id" to paypalHttpHeaderProvider.getMetadataId(),
      "authorization" to "Bearer $ewt"
    ),
    query = mapOf("wallet.address" to walletAddress),
    body = null,
    timeout = Duration.ofSeconds(30),
  ).let { true }

  override suspend fun getPaypalTransaction(
    uId: String,
    walletAddress: String,
    walletSignature: String,
  ) = restClient.get<TransactionResponse>(
    path = "broker/8.20230522/transactions/$uId",
    query = mapOf(
      "wallet.address" to walletAddress,
      "wallet.signature" to walletSignature
    ),
    timeout = Duration.ofSeconds(30),
  )
}

internal interface PaypalRepository {

  suspend fun createTransaction(
    ewt: String,
    walletAddress: String,
    paymentDetails: PaymentDetailsRequest,
  ): TransactionResponse

  suspend fun createToken(
    ewt: String,
    walletAddress: String,
    cancelUrl: String,
    returnUrl: String,
  ): TokenData

  suspend fun cancelToken(
    ewt: String,
    walletAddress: String,
    token: String,
  ): Boolean

  suspend fun createBillingAgreement(
    ewt: String,
    walletAddress: String,
    token: String,
  ): BillingAgreement

  suspend fun getCurrentBillingAgreement(
    ewt: String,
    walletAddress: String,
  ): BillingAgreement

  suspend fun removeBillingAgreement(
    ewt: String,
    walletAddress: String,
  ): Boolean

  suspend fun getPaypalTransaction(
    uId: String,
    walletAddress: String,
    walletSignature: String,
  ): TransactionResponse
}
