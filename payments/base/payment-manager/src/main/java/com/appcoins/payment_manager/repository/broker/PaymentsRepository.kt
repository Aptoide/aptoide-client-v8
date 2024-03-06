package com.appcoins.payment_manager.repository.broker

import com.appcoins.payment_manager.repository.broker.model.PaymentMethodsResponse
import com.appcoins.payments.arch.PaymentMethodData
import com.appcoins.payments.arch.PaymentMethods
import com.appcoins.payments.network.RestClient
import com.appcoins.payments.network.get
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class PaymentsRepositoryImpl @Inject constructor(
  private val restClient: RestClient,
) : PaymentsRepository {

  override suspend fun getPaymentMethods(
    domain: String,
    priceCurrency: String,
    priceValue: String,
  ): PaymentMethods {
    val paymentMethods = restClient.get<PaymentMethodsResponse>(
      path = "broker/8.20230522/methods",
      query = mapOf(
        "price.currency" to priceCurrency,
        "price.value" to priceValue,
        "domain" to domain,
        "currency.type" to "fiat",
      ),
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
}

interface PaymentsRepository {
  suspend fun getPaymentMethods(
    domain: String,
    priceCurrency: String,
    priceValue: String,
  ): PaymentMethods
}