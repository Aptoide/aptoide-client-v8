package cm.aptoide.pt.payment_method.adyen.repository

import cm.aptoide.pt.payment_method.adyen.PaymentMethodDetailsData
import cm.aptoide.pt.payment_method.adyen.repository.model.PaymentMethodDetailsResponse
import org.json.JSONObject
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AdyenV2RepositoryImpl @Inject constructor(
  private val adyenV2Api: AdyenV2Api,
) : AdyenV2Repository {

  override suspend fun getPaymentMethodDetails(
    walletAddress: String,
    priceValue: String,
    priceCurrency: String,
  ): PaymentMethodDetailsData {
    val response = adyenV2Api.getPaymentMethodDetails(
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

  internal interface AdyenV2Api {

    @GET("broker/8.20200815/gateways/adyen_v2/payment-methods")
    suspend fun getPaymentMethodDetails(
      @Query("wallet.address") walletAddress: String,
      @Query("price.value") priceValue: String,
      @Query("price.currency") priceCurrency: String,
      @Query("method") method: String = "credit_card",
    ): PaymentMethodDetailsResponse
  }
}

internal interface AdyenV2Repository {
  suspend fun getPaymentMethodDetails(
    walletAddress: String,
    priceValue: String,
    priceCurrency: String,
  ): PaymentMethodDetailsData
}
