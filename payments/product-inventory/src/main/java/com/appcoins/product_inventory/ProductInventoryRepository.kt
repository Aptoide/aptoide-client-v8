package com.appcoins.product_inventory

import com.appcoins.payments.arch.ProductInfoData
import com.appcoins.payments.arch.PurchaseInfoData
import com.appcoins.payments.arch.PurchaseState
import com.appcoins.payments.arch.TransactionPrice
import com.appcoins.payments.network.RestClient
import com.appcoins.payments.network.get
import com.appcoins.payments.network.post
import com.appcoins.product_inventory.model.ConsumablesResponse
import com.appcoins.product_inventory.model.ProductInfoResponse
import com.appcoins.product_inventory.model.PurchaseResponse
import com.appcoins.product_inventory.model.PurchaseStateResponse
import com.appcoins.product_inventory.model.PurchasesResponse

internal class ProductInventoryRepositoryImpl(
  private val restClient: RestClient,
) : ProductInventoryRepository {

  override suspend fun isInAppBillingSupported(packageName: String): Boolean =
    restClient.get<Boolean>(path = "productv2/8.20230522/applications/$packageName/inapp")

  override suspend fun getConsumables(
    packageName: String,
    names: String,
  ): List<ProductInfoData> = restClient
    .get<ConsumablesResponse>(
      path = "productv2/8.20230522/applications/$packageName/inapp/consumables",
      query = mapOf("skus" to names)
    )
    .items
    .map(ProductInfoResponse::toProductInfoData)

  override suspend fun getProductInfo(
    name: String,
    sku: String?,
    currency: String?,
    country: String?,
  ): ProductInfoData = restClient.get<ProductInfoResponse>(
    path = "productv2/8.20230522/applications/$name/inapp/consumables/$sku",
    query = mapOf(
      "currency" to currency,
      "country" to country
    ),
  ).toProductInfoData()

  override suspend fun getPurchases(
    packageName: String,
    ewt: String,
  ): List<PurchaseInfoData> = restClient
    .get<PurchasesResponse>(
      path = "productv2/8.20230522/applications/$packageName/inapp/consumable/purchases",
      header = mapOf("authorization" to "Bearer $ewt"),
      query = mapOf(
        "type" to "INAPP",
        "state" to "PENDING",
        "sku" to null,
      ),
    )
    .items
    .map { it.toPurchaseInfoData(packageName) }

  override suspend fun consumePurchase(
    domain: String,
    uid: String,
    authorization: String,
    payload: String?,
  ): Boolean = restClient.post<Unit>(
    path = "productv2/8.20230522/applications/$domain/inapp/purchases/$uid/consume",
    header = mapOf("authorization" to "Bearer $authorization"),
    query = mapOf("payload" to payload)
  ).let { true }
}

interface ProductInventoryRepository {

  suspend fun isInAppBillingSupported(
    packageName: String,
  ): Boolean

  suspend fun getConsumables(
    packageName: String,
    names: String,
  ): List<ProductInfoData>

  suspend fun getProductInfo(
    name: String,
    sku: String? = null,
    currency: String? = null,
    country: String? = null,
  ): ProductInfoData

  suspend fun getPurchases(
    packageName: String,
    ewt: String,
  ): List<PurchaseInfoData>

  suspend fun consumePurchase(
    domain: String,
    uid: String,
    authorization: String,
    payload: String? = null,
  ): Boolean
}

private fun ProductInfoResponse.toProductInfoData() = ProductInfoData(
  sku = this.sku,
  title = this.title,
  description = this.description,
  priceValue = this.price.value,
  priceCurrency = this.price.currency,
  priceInDollars = this.price.usd.value,
  billingType = "inapp",
  transactionPrice = TransactionPrice(
    base = this.price.currency,
    appcoinsAmount = this.price.appc.value.toDouble(),
    amount = this.price.value.toDouble(),
    currency = this.price.currency,
    currencySymbol = this.price.symbol
  ),
  subscriptionPeriod = null,
  trialPeriod = null
)

private fun PurchaseResponse.toPurchaseInfoData(packageName: String) = PurchaseInfoData(
  uid = this.uid,
  productName = this.sku,
  state = when (this.state) {
    PurchaseStateResponse.PENDING -> PurchaseState.PENDING
    PurchaseStateResponse.ACKNOWLEDGED -> PurchaseState.ACKNOWLEDGED
    PurchaseStateResponse.CONSUMED -> PurchaseState.CONSUMED
  },
  autoRenewing = false,
  renewal = null,
  packageName = packageName,
  signatureValue = this.verification.signature,
  signatureMessage = this.verification.data,
)
