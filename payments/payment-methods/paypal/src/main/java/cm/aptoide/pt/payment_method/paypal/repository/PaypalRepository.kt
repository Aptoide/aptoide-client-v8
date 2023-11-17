package cm.aptoide.pt.payment_method.paypal.repository

import cm.aptoide.pt.payment_method.paypal.model.BillingAgreement
import cm.aptoide.pt.payment_method.paypal.model.TokenData
import cm.aptoide.pt.payment_method.paypal.repository.model.BillingAgreementRequest
import cm.aptoide.pt.payment_method.paypal.repository.model.BillingAgreementResponse
import cm.aptoide.pt.payment_method.paypal.repository.model.PaymentDetailsRequest
import cm.aptoide.pt.payment_method.paypal.repository.model.TokenResponse
import cm.aptoide.pt.payment_method.paypal.repository.model.TransactionResponse
import cm.aptoide.pt.payment_method.paypal.repository.model.Urls
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class PaypalRepositoryImpl @Inject constructor(
  private val paypalV2Api: PaypalV2Api,
  private val paypalHttpHeaderProvider: PaypalHttpHeadersProvider,
) : PaypalRepository {

  override suspend fun createTransaction(
    ewt: String,
    walletAddress: String,
    paymentDetails: PaymentDetailsRequest,
  ): TransactionResponse =
    try {
      paypalV2Api.createTransaction(
        ewt = "Bearer $ewt",
        walletAddress = walletAddress,
        paymentDetails = paymentDetails,
        paypalMetadataId = paypalHttpHeaderProvider.getMetadataId()
      )
    } catch (e: HttpException) {
      throw handleHttpException(e)
    }

  private fun handleHttpException(httpException: HttpException): Throwable {
    if (httpException.code() == 404) {
      val errorContent = httpException.response()?.errorBody()?.string()
      if (errorContent != null) {
        val errorJson = JSONObject(errorContent)
        if (errorJson.getString("code") == "Paypal.BillingAgreement.NotFound")
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
    val token = paypalV2Api.createToken(
      ewt = "Bearer $ewt",
      walletAddress = walletAddress,
      paypalMetadataId = paypalHttpHeaderProvider.getMetadataId(),
      billingAgreementRequest = BillingAgreementRequest(
        urls = Urls(
          returnUrl = returnUrl,
          cancelUrl = cancelUrl
        )
      )
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
  ): Boolean = paypalV2Api.cancelToken(
    ewt = "Bearer $ewt",
    walletAddress = walletAddress,
    token = token,
    paypalMetadataId = paypalHttpHeaderProvider.getMetadataId(),
  ).isSuccessful

  override suspend fun createBillingAgreement(
    ewt: String,
    walletAddress: String,
    token: String,
  ): BillingAgreement {
    val billingAgreementResponse = paypalV2Api.createBillingAgreement(
      paypalMetadataId = paypalHttpHeaderProvider.getMetadataId(),
      ewt = "Bearer $ewt",
      walletAddress = walletAddress,
      token = token
    )
    return BillingAgreement(
      uid = billingAgreementResponse.uid
    )
  }

  override suspend fun getCurrentBillingAgreement(
    ewt: String,
    walletAddress: String,
  ): BillingAgreement {
    val response = paypalV2Api.getCurrentBillingAgreement(
      paypalMetadataId = paypalHttpHeaderProvider.getMetadataId(),
      ewt = "Bearer $ewt",
      walletAddress = walletAddress,
    )
    return BillingAgreement(
      uid = response.uid
    )
  }

  override suspend fun removeBillingAgreement(
    ewt: String,
    walletAddress: String,
  ): Boolean =
    paypalV2Api.removeBillingAgreement(
      paypalMetadataId = paypalHttpHeaderProvider.getMetadataId(),
      ewt = "Bearer $ewt",
      walletAddress = walletAddress,
    ).isSuccessful

  internal interface PaypalV2Api {

    @POST("broker/8.20230522/gateways/paypal/transactions")
    suspend fun createTransaction(
      @Header("PayPal-Client-Metadata-Id") paypalMetadataId: String,
      @Header("authorization") ewt: String,
      @Query("wallet.address") walletAddress: String,
      @Body paymentDetails: PaymentDetailsRequest,
    ): TransactionResponse

    @POST("broker/8.20230522/gateways/paypal/billing-agreement/token/create")
    suspend fun createToken(
      @Header("PayPal-Client-Metadata-Id") paypalMetadataId: String,
      @Header("authorization") ewt: String,
      @Query("wallet.address") walletAddress: String,
      @Body billingAgreementRequest: BillingAgreementRequest,
    ): TokenResponse

    @POST("broker/8.20230522/gateways/paypal/billing-agreement/create")
    suspend fun createBillingAgreement(
      @Header("PayPal-Client-Metadata-Id") paypalMetadataId: String,
      @Header("authorization") ewt: String,
      @Query("wallet.address") walletAddress: String,
      @Body token: String,
    ): BillingAgreementResponse

    @POST("broker/8.20230522/gateways/paypal/billing-agreement/token/cancel")
    suspend fun cancelToken(
      @Header("PayPal-Client-Metadata-Id") paypalMetadataId: String,
      @Header("authorization") ewt: String,
      @Query("wallet.address") walletAddress: String,
      @Body token: String,
    ): Response<Unit>

    @GET("broker/8.20230522/gateways/paypal/billing-agreement")
    suspend fun getCurrentBillingAgreement(
      @Header("PayPal-Client-Metadata-Id") paypalMetadataId: String,
      @Header("authorization") ewt: String,
      @Query("wallet.address") walletAddress: String,
    ): BillingAgreementResponse

    @POST("broker/8.20230522/gateways/paypal/billing-agreement/cancel")
    suspend fun removeBillingAgreement(
      @Header("PayPal-Client-Metadata-Id") paypalMetadataId: String,
      @Header("authorization") ewt: String,
      @Query("wallet.address") walletAddress: String,
    ): Response<Unit>
  }
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
}
