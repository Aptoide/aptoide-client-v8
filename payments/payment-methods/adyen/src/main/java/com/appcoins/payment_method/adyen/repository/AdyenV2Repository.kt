package com.appcoins.payment_method.adyen.repository

import com.appcoins.payment_method.adyen.ClearRecurringDetails
import com.appcoins.payment_method.adyen.PaymentDetails
import com.appcoins.payment_method.adyen.PaymentMethodDetailsData
import com.appcoins.payment_method.adyen.repository.model.AdyenPayment
import com.appcoins.payment_method.adyen.repository.model.PaymentMethodDetailsResponse
import com.appcoins.payment_method.adyen.repository.model.ResponseErrorBody
import com.appcoins.payment_method.adyen.repository.model.TransactionResponse
import com.appcoins.payment_method.adyen.repository.model.mapAdyenRefusalCode
import com.appcoins.payments.network.HttpException
import com.appcoins.payments.network.RestClient
import com.appcoins.payments.network.get
import com.appcoins.payments.network.patch
import com.appcoins.payments.network.post
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AdyenV2RepositoryImpl @Inject constructor(
  private val restClient: RestClient,
) : AdyenV2Repository {

  override suspend fun getPaymentMethodDetails(
    ewt: String,
    walletAddress: String,
    priceValue: String,
    priceCurrency: String,
  ): PaymentMethodDetailsData = try {
    val response = restClient.get<PaymentMethodDetailsResponse>(
      path = "broker/8.20230522/gateways/adyen_v2/payment-methods",
      header = mapOf("authorization" to "Bearer $ewt"),
      query = mapOf(
        "wallet.address" to walletAddress,
        "price.value" to priceValue,
        "price.currency" to priceCurrency,
        "method" to "credit_card",
      ),
    )

    PaymentMethodDetailsData(
      price = response.price.value.toDouble(),
      currency = response.price.currency,
      json = JSONObject(response.payment.toString())
    )
  } catch (e: Throwable) {
    throw extractAdyenException(e)
  }

  override suspend fun createTransaction(
    ewt: String,
    walletAddress: String,
    paymentDetails: PaymentDetails,
  ): TransactionResponse {
    val response = try {
      restClient.post<TransactionResponse>(
        path = "broker/8.20230522/gateways/adyen_v2/transactions",
        header = mapOf("authorization" to "Bearer $ewt"),
        query = mapOf("wallet.address" to walletAddress),
        body = paymentDetails
      )
    } catch (e: Throwable) {
      throw extractAdyenException(e)
    }
    response.mapAdyenRefusalCode()?.let { throw it }

    return response
  }

  override suspend fun submitActionResult(
    ewt: String,
    walletAddress: String,
    uid: String,
    paymentData: String?,
    paymentDetails: JSONObject?,
  ): TransactionResponse {
    val response = try {
      restClient.patch<TransactionResponse>(
        path = "broker/8.20230522/gateways/adyen_v2/transactions/$uid",
        header = mapOf("authorization" to "Bearer $ewt"),
        query = mapOf("wallet.address" to walletAddress),
        body = AdyenPayment(
          data = paymentData,
          details = paymentDetails.toJsonObject()
        )
      )
    } catch (e: Throwable) {
      throw extractAdyenException(e)
    }

    response.mapAdyenRefusalCode()?.let { throw it }
    return response
  }

  override suspend fun clearStoredCard(walletAddress: String): Boolean {
    return try {
      restClient.post<Unit>(
        path = "broker/8.20230522/gateways/adyen_v2/disable-recurring",
        body = ClearRecurringDetails(walletAddress = walletAddress)
      )
      true
    } catch (e: Throwable) {
      false
    }
  }

  override suspend fun getCreditCardTransaction(
    uId: String,
    walletAddress: String,
    walletSignature: String,
  ): TransactionResponse = restClient.get<TransactionResponse>(
    path = "broker/8.20230522/transactions/$uId",
    query = mapOf(
      "wallet.address" to walletAddress,
      "wallet.signature" to walletSignature
    ),
  )

  private fun extractAdyenException(error: Throwable): Exception =
    if (error is IOException || error.cause is IOException) {
      NoNetworkException()
    } else if (error is HttpException) {
      val httpCode = error.code
      val (messageCode, _, text, data) = error.parseBodyTo<ResponseErrorBody>()
        ?: ResponseErrorBody(code = null, path = null, text = null, data = null)

      when {
        httpCode == 403 -> FraudException(text)
        httpCode == 409 -> ConflictException(text)
        httpCode == 400 && messageCode == FIELDS_MISSING_CODE
          && text?.contains("payment.billing") == true ->
          MissingBillingAddressException(text)

        messageCode == NOT_ALLOWED_CODE -> NotAllowedException(text)
        messageCode == FORBIDDEN_CODE -> ForbiddenException(text)

        messageCode == ADYEN_V2_ERROR && (data is Number) -> mapAdyenErrorCode(data.toInt(), text)

        else -> Exception(error.body ?: error.message)
      }
    } else {
      Exception()
    }

  private fun mapAdyenErrorCode(code: Int, message: String?) = when (code) {
    101 -> InvalidCardException(message)
    103 -> CvcLengthException(message)
    105 -> CardSecurityException(message)
    138 -> CurrencyNotSupportedException(message)
    200 -> InvalidCountryCodeException(message)
    172, 174, 422, 800 -> OutdatedCardException(message)
    704 -> AlreadyProcessedException(message)
    905 -> PaymentErrorException(message)
    907 -> PaymentNotSupportedException(message)
    916 -> TransactionAmountExceededException(message)
    else -> Exception(message)
  }

  internal companion object {
    internal const val NOT_ALLOWED_CODE = "NotAllowed"
    internal const val FORBIDDEN_CODE = "Authorization.Forbidden"
    internal const val FIELDS_MISSING_CODE = "Body.Fields.Missing"
    internal const val ADYEN_V2_ERROR = "AdyenV2.Error"
  }
}

internal interface AdyenV2Repository {
  suspend fun getPaymentMethodDetails(
    ewt: String,
    walletAddress: String,
    priceValue: String,
    priceCurrency: String,
  ): PaymentMethodDetailsData

  suspend fun createTransaction(
    ewt: String,
    walletAddress: String,
    paymentDetails: PaymentDetails,
  ): TransactionResponse

  suspend fun submitActionResult(
    ewt: String,
    walletAddress: String,
    uid: String,
    paymentData: String?,
    paymentDetails: JSONObject?,
  ): TransactionResponse

  suspend fun clearStoredCard(
    walletAddress: String,
  ): Boolean

  suspend fun getCreditCardTransaction(
    uId: String,
    walletAddress: String,
    walletSignature: String,
  ): TransactionResponse
}

private fun JSONObject?.toJsonObject(): JsonObject {
  if (this == null) return JsonObject()

  return JsonParser().parse(this.toString()).asJsonObject
}
