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
import com.google.gson.Gson
import java.time.Duration

internal class PaypalRepositoryImpl(
  private val restClient: RestClient,
  private val paypalHttpHeaderProvider: PaypalHttpHeadersProvider,
) : PaypalRepository {

  override suspend fun createTransaction(
    ewt: String,
    walletAddress: String,
    paymentDetails: PaymentDetailsRequest,
  ): TransactionResponse =
    try {
      restClient.post(
        path = "broker/8.20230522/gateways/paypal/transactions",
        header = mapOf(
          "PayPal-Client-Metadata-Id" to paypalHttpHeaderProvider.getMetadataId(),
          "authorization" to "Bearer $ewt"
        ),
        query = mapOf("wallet.address" to walletAddress),
        body = paymentDetails.let { Gson().toJson(it) },
        timeout = Duration.ofSeconds(30),
      )?.let { Gson().fromJson(it, TransactionResponse::class.java) }!!
    } catch (e: HttpException) {
      throw handleHttpException(e)
    }

  private fun handleHttpException(httpException: HttpException): Throwable {
    if (httpException.code == 404) {
      val transactionError = httpException.body
        ?.let { Gson().fromJson(it, TransactionError::class.java) }
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
    val token = restClient.post(
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
      ).let { Gson().toJson(it) },
      timeout = Duration.ofSeconds(30),
    )?.let { Gson().fromJson(it, TokenResponse::class.java) }!!

    return TokenData(
      token = token.token,
      url = token.redirect.url
    )
  }

  override suspend fun cancelToken(
    ewt: String,
    walletAddress: String,
    token: String,
  ): Boolean = restClient.post(
    path = "broker/8.20230522/gateways/paypal/billing-agreement/token/cancel",
    header = mapOf(
      "PayPal-Client-Metadata-Id" to paypalHttpHeaderProvider.getMetadataId(),
      "authorization" to "Bearer $ewt"
    ),
    query = mapOf("wallet.address" to walletAddress),
    body = token.let { Gson().toJson(it) },
    timeout = Duration.ofSeconds(30),
  ).let { true }

  override suspend fun createBillingAgreement(
    ewt: String,
    walletAddress: String,
    token: String,
  ): BillingAgreement {
    val billingAgreementResponse = restClient.post(
      path = "broker/8.20230522/gateways/paypal/billing-agreement/create",
      header = mapOf(
        "PayPal-Client-Metadata-Id" to paypalHttpHeaderProvider.getMetadataId(),
        "authorization" to "Bearer $ewt"
      ),
      query = mapOf("wallet.address" to walletAddress),
      body = token.let { Gson().toJson(it) },
      timeout = Duration.ofSeconds(30),
    )?.let { Gson().fromJson(it, BillingAgreementResponse::class.java) }!!
    return BillingAgreement(uid = billingAgreementResponse.uid)
  }

  override suspend fun getCurrentBillingAgreement(
    ewt: String,
    walletAddress: String,
  ): BillingAgreement {
    val response = restClient.get(
      path = "broker/8.20230522/gateways/paypal/billing-agreement",
      header = mapOf(
        "PayPal-Client-Metadata-Id" to paypalHttpHeaderProvider.getMetadataId(),
        "authorization" to "Bearer $ewt"
      ),
      query = mapOf("wallet.address" to walletAddress),
      timeout = Duration.ofSeconds(30),
    )?.let { Gson().fromJson(it, BillingAgreementResponse::class.java) }!!
    return BillingAgreement(uid = response.uid)
  }

  override suspend fun removeBillingAgreement(
    ewt: String,
    walletAddress: String,
  ): Boolean = restClient.post(
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
  ) = restClient.get(
    path = "broker/8.20230522/transactions/$uId",
    query = mapOf(
      "wallet.address" to walletAddress,
      "wallet.signature" to walletSignature
    ),
    timeout = Duration.ofSeconds(30),
  )?.let { Gson().fromJson(it, TransactionResponse::class.java) }!!
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
