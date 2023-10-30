package cm.aptoide.pt.payment_method.adyen.repository

import cm.aptoide.pt.payment_method.adyen.PaymentDetails
import cm.aptoide.pt.payment_method.adyen.PaymentMethodDetailsData
import cm.aptoide.pt.payment_method.adyen.repository.model.AdyenPayment
import cm.aptoide.pt.payment_method.adyen.repository.model.PaymentMethodDetailsResponse
import cm.aptoide.pt.payment_method.adyen.repository.model.TransactionResponse
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.json.JSONObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
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
  ): PaymentMethodDetailsData {
    val response = adyenV2Api.getPaymentMethodDetails(
      ewt = "Bearer $ewt",
      walletAddress = walletAddress,
      priceValue = priceValue,
      priceCurrency = priceCurrency,
    )

    return PaymentMethodDetailsData(
      price = response.price.value.toDouble(),
      currency = response.price.currency,
      json = JSONObject(response.payment.toString())
    )
  }

  override suspend fun createTransaction(
    ewt: String,
    walletAddress: String,
    paymentDetails: PaymentDetails,
  ): TransactionResponse = adyenV2Api.createTransaction(
    ewt = "Bearer $ewt",
    walletAddress = walletAddress,
    paymentDetails = paymentDetails
  )

  override suspend fun submitActionResult(
    ewt: String,
    walletAddress: String,
    uid: String,
    paymentData: String?,
    paymentDetails: JSONObject?,
  ): TransactionResponse = adyenV2Api.submitActionResult(
    ewt = "Bearer $ewt",
    walletAddress = walletAddress,
    uid = uid,
    request = AdyenPayment(
      data = paymentData,
      details = paymentDetails.toJsonObject()
    )
  )

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

    @PATCH("broker/8.20200815/gateways/adyen_v2/transactions/{uid}")
    suspend fun submitActionResult(
      @Path("uid") uid: String,
      @Header("authorization") ewt: String,
      @Query("wallet.address") walletAddress: String,
      @Body request: AdyenPayment,
    ): TransactionResponse
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
}

private fun JSONObject?.toJsonObject(): JsonObject {
  if (this == null) return JsonObject()

  return JsonParser.parseString(this.toString()).asJsonObject
}
