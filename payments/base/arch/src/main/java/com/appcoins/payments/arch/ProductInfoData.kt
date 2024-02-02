package com.appcoins.payments.arch

import java.util.Locale
import kotlin.random.Random

data class ProductInfoData(
  val sku: String,
  val title: String,
  val description: String?,
  val priceValue: String,
  val priceCurrency: String,
  val priceInDollars: String,
  val billingType: String,
  val transactionPrice: TransactionPrice,
  val subscriptionPeriod: String? = null, //Subs only
  val trialPeriod: String? = null, //Subs only
) {
  fun getBasePriceInMicro(appcName: String) =
    if (appcName.equals(transactionPrice.base, true)) {
      getAppcPriceInMicro()
    } else {
      getFiatPriceInMicro()
    }

  fun getBaseCurrency(appcName: String) =
    if (appcName.equals(transactionPrice.base, true)) {
      appcName
    } else {
      transactionPrice.currency
    }

  fun getBasePrice(appcName: String) =
    if (appcName.equals(transactionPrice.base, true)) {
      getAppcPrice(appcName)
    } else {
      getFiatPrice()
    }

  fun getAppcPrice(appcName: String) =
    String.format("%s %s", appcName, transactionPrice.appcoinsAmount)

  fun getFiatPrice() =
    String.format(
      Locale.US,
      "%s %s",
      transactionPrice.currencySymbol,
      transactionPrice.amount
    )

  fun getAppcPriceInMicro() = (transactionPrice.appcoinsAmount * 1000000).toLong()

  fun getFiatPriceInMicro() = (transactionPrice.amount * 1000000).toLong()
}

data class TransactionPrice(
  val base: String?,
  val appcoinsAmount: Double,
  val amount: Double,
  val currency: String,
  val currencySymbol: String,
)

val emptyTransactionPrice = TransactionPrice(
  base = null,
  appcoinsAmount = Random.nextDouble(1.0, 1000.0),
  amount = Random.nextDouble(1.0, 1000.0),
  currency = "EUR",
  currencySymbol = "€"
)

val emptyProductInfoData = ProductInfoData(
  sku = "ProductInfoData sku",
  title = "ProductInfoData title",
  description = "ProductInfoData description",
  priceValue = "ProductInfoData price value",
  priceCurrency = "",
  priceInDollars = "ProductInfoData price value in dollars",
  billingType = "inapp",
  transactionPrice = emptyTransactionPrice,
  subscriptionPeriod = null,
  trialPeriod = null
)
