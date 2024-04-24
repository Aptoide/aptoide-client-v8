package com.appcoins.payments.sdk.sku_details

import com.appcoins.payments.arch.ProductInfoData

internal interface ProductSerializer {
  fun serialize(products: List<ProductInfoData>): ArrayList<String>
}

internal class ProductSerializerImpl : ProductSerializer {

  companion object {
    internal const val APPC = "APPC"
  }

  override fun serialize(products: List<ProductInfoData>): ArrayList<String> {
    return ArrayList(products.map { toProduct(it).toJsonString() })
  }

  private fun toProduct(productInfoData: ProductInfoData) = SKU(
    productId = productInfoData.sku,
    type = productInfoData.billingType,
    priceBase = productInfoData.getBasePrice(APPC),
    currencyBase = productInfoData.getBaseCurrency(APPC),
    amountBase = productInfoData.getBasePriceInMicro(APPC),
    priceAppc = productInfoData.getAppcPrice(APPC),
    currencyAppc = APPC,
    amountAppc = productInfoData.getAppcPriceInMicro(),
    priceFiat = productInfoData.getFiatPrice(),
    currencyFiat = productInfoData.transactionPrice.currency,
    amountFiat = productInfoData.getFiatPriceInMicro(),
    title = productInfoData.title,
    description = productInfoData.description,
    subscriptionPeriod = productInfoData.subscriptionPeriod,
    trialPeriod = productInfoData.trialPeriod,
  )
}
