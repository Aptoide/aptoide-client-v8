package com.appcoins.payment_method.adyen.repository

import com.appcoins.payment_method.adyen.ClearRecurringDetails
import com.appcoins.payment_method.adyen.PaymentDetails
import com.appcoins.payment_method.adyen.PaymentMethodDetailsData
import com.appcoins.payment_method.adyen.repository.model.AdyenPayment
import com.appcoins.payment_method.adyen.repository.model.PaymentMethodDetailsResponse
import com.appcoins.payment_method.adyen.repository.model.ResponseErrorBody
import com.appcoins.payment_method.adyen.repository.model.TransactionResponse
import com.appcoins.payment_method.adyen.repository.model.mapAdyenRefusalCode
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AdyenV2RepositoryImpl @Inject constructor(
  private val adyenV2Api: AdyenV2Api,
) : AdyenV2Repository {

  override suspend fun getPaymentMethodDetails(
    ewt: String,
    walletAddress: String,
    priceValue: String,
    priceCurrency: String,
  ): PaymentMethodDetailsData = try {
    val response = adyenV2Api.getPaymentMethodDetails(
      ewt = "Bearer $ewt",
      walletAddress = walletAddress,
      priceValue = priceValue,
      priceCurrency = priceCurrency,
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
      adyenV2Api.createTransaction(
        ewt = "Bearer $ewt",
        walletAddress = walletAddress,
        paymentDetails = paymentDetails
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
      adyenV2Api.submitActionResult(
        ewt = "Bearer $ewt",
        walletAddress = walletAddress,
        uid = uid,
        request = AdyenPayment(
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
      val response = adyenV2Api.clearStoredCard(
        ClearRecurringDetails(walletAddress = walletAddress)
      )
      response.code() == 204
    } catch (e: Throwable) {
      false
    }
  }

  override suspend fun getCreditCardTransaction(
    uId: String,
    walletAddress: String,
    walletSignature: String,
  ): TransactionResponse =
    adyenV2Api.getCreditCardTransaction(
      uId = uId,
      walletAddress = walletAddress,
      walletSignature = walletSignature,
    )

  private fun extractAdyenException(e: Throwable): Exception =
    if (e is IOException || e.cause is IOException) {
      NoNetworkException()
    } else if (e is HttpException) {
      val httpCode = e.code()

      val reader = e.response()?.errorBody()?.charStream()
      val message = reader?.readText()?.takeIf { it.isNotBlank() } ?: e.message()
      reader?.close()

      val (messageCode, _, text, data) = JSONObject(message).run {
        ResponseErrorBody(
          code = getString("code"),
          path = getString("path"),
          text = getString("text"),
          data = get("data"),
        )
      }

      when {
        httpCode == 403 -> FraudException(text)
        httpCode == 409 -> ConflictException(text)
        httpCode == 400 && messageCode == FIELDS_MISSING_CODE
          && text?.contains("payment.billing") == true ->
          MissingBillingAddressException(text)

        messageCode == NOT_ALLOWED_CODE -> NotAllowedException(text)
        messageCode == FORBIDDEN_CODE -> ForbiddenException(text)

        messageCode == ADYEN_V2_ERROR && (data is Number) -> mapAdyenErrorCode(data.toInt(), text)

        else -> Exception(message)
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

  internal interface AdyenV2Api {

    @GET("broker/8.20230522/gateways/adyen_v2/payment-methods")
    suspend fun getPaymentMethodDetails(
      @Header("authorization") ewt: String,
      @Query("wallet.address") walletAddress: String,
      @Query("price.value") priceValue: String,
      @Query("price.currency") priceCurrency: String,
      @Query("method") method: String = "credit_card",
    ): PaymentMethodDetailsResponse

    @POST("broker/8.20230522/gateways/adyen_v2/transactions")
    suspend fun createTransaction(
      @Header("authorization") ewt: String,
      @Query("wallet.address") walletAddress: String,
      @Body paymentDetails: PaymentDetails,
    ): TransactionResponse

    @PATCH("broker/8.20230522/gateways/adyen_v2/transactions/{uid}")
    suspend fun submitActionResult(
      @Path("uid") uid: String,
      @Header("authorization") ewt: String,
      @Query("wallet.address") walletAddress: String,
      @Body request: AdyenPayment,
    ): TransactionResponse

    @POST("broker/8.20230522/gateways/adyen_v2/disable-recurring")
    suspend fun clearStoredCard(
      @Body clearRecurringDetails: ClearRecurringDetails,
    ): Response<Any>

    @GET("broker/8.20230522/transactions/{uId}")
    suspend fun getCreditCardTransaction(
      @Path("uId") uId: String,
      @Query("wallet.address") walletAddress: String,
      @Query("wallet.signature") walletSignature: String,
    ): TransactionResponse
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
