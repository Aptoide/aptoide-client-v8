package com.appcoins.payment_manager.repository.broker

import com.appcoins.payment_manager.repository.broker.domain.PaymentMethodData
import com.appcoins.payment_manager.repository.broker.domain.PaymentMethods
import com.appcoins.payment_manager.repository.broker.model.PaymentMethodsResponse
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class BrokerRepositoryImpl @Inject constructor(
  private val brokerApi: BrokerApi,
) : BrokerRepository {

  override suspend fun getPaymentMethods(
    domain: String,
    priceCurrency: String,
    priceValue: String,
  ): PaymentMethods {
    val paymentMethods = brokerApi.getPaymentMethods(
      priceCurrency = priceCurrency,
      priceValue = priceValue,
      domain = domain,
    )

    return PaymentMethods(
      items = paymentMethods.items.map {
        PaymentMethodData(
          id = it.name,
          label = it.label,
          iconUrl = it.icon,
          available = it.isAvailable
        )
      }
    )
  }

  internal interface BrokerApi {

    @GET("broker/8.20200815/methods")
    suspend fun getPaymentMethods(
      @Query("price.currency") priceCurrency: String,
      @Query("price.value") priceValue: String,
      @Query("domain") domain: String?,
      @Query("currency.type") currencyType: String = "fiat",
    ): PaymentMethodsResponse
  }
}

interface BrokerRepository {
  suspend fun getPaymentMethods(
    domain: String,
    priceCurrency: String,
    priceValue: String,
  ): PaymentMethods
}
