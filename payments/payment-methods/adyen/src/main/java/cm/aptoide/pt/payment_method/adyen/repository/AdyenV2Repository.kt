package cm.aptoide.pt.payment_method.adyen.repository

import cm.aptoide.pt.payment_method.adyen.PaymentDetails
import cm.aptoide.pt.payment_method.adyen.PaymentMethodDetailsData
import cm.aptoide.pt.payment_method.adyen.repository.model.PaymentMethodDetailsResponse
import cm.aptoide.pt.payment_method.adyen.repository.model.ResponseErrorBody
import cm.aptoide.pt.payment_method.adyen.repository.model.TransactionResponse
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
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

    return response
  }

  private fun extractAdyenException(e: Throwable): Exception =
    if (e is IOException || e.cause is IOException) {
      NoNetworkException()
    } else if (e is HttpException) {
      val httpCode = e.code()

      val reader = e.response()?.errorBody()?.charStream()
      val message = reader?.readText()?.takeIf { it.isNotBlank() } ?: e.message()
      reader?.close()

      val (messageCode, _, text, data) = Gson().fromJson(message, ResponseErrorBody::class.java)

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

  private fun mapAdyenRefusalCode(refusalCode: Int, refusalReason: String): AdyenRefusalException =
    when (refusalCode) {
      2 -> DeclinedException(refusalReason)
      3 -> ReferralException(refusalReason)
      4 -> AcquirerErrorException(refusalReason)
      5 -> BlockedCardException(refusalReason)
      6 -> ExpiredCardException(refusalReason)
      7 -> InvalidAmountException(refusalReason)
      8 -> InvalidCardNumberException(refusalReason)
      9 -> IssuerUnavailableException(refusalReason)
      10 -> NotSupportedException(refusalReason)
      11 -> Not3dAuthenticatedException(refusalReason)
      12 -> NotEnoughBalanceException(refusalReason)
      17 -> IncorrectOnlinePinException(refusalReason)
      18 -> PinTriesExceededException(refusalReason)
      20 -> FraudRefusalException(refusalReason)
      22 -> CancelledDueToFraudException(refusalReason)
      23 -> TransactionNotPermittedException(refusalReason)
      24 -> CvcDeclinedException(refusalReason)
      25 -> RestrictedCardException(refusalReason)
      26 -> RevocationOfAuthException(refusalReason)
      27 -> DeclinedNonGenericException(refusalReason)
      28 -> WithdrawAmountExceededException(refusalReason)
      31 -> IssuerSuspectedFraudException(refusalReason)
      else -> AdyenRefusalException(refusalReason)
    }

  internal interface AdyenV2Api {

    @GET("broker/8.20200815/gateways/adyen_v2/payment-methods")
    suspend fun getPaymentMethodDetails(
      @Header("authorization") ewt: String,
      @Query("wallet.address") walletAddress: String,
      @Query("price.value") priceValue: String,
      @Query("price.currency") priceCurrency: String,
      @Query("method") method: String = "credit_card",
    ): PaymentMethodDetailsResponse

    @POST("broker/8.20200815/gateways/adyen_v2/transactions")
    suspend fun createTransaction(
      @Header("authorization") ewt: String,
      @Query("wallet.address") walletAddress: String,
      @Body paymentDetails: PaymentDetails,
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
}
