package com.appcoins.product_inventory

import com.appcoins.payments.arch.ProductInfoData
import com.appcoins.payments.arch.PurchaseInfoData
import com.appcoins.payments.arch.PurchaseState
import com.appcoins.payments.arch.TransactionPrice
import com.appcoins.product_inventory.model.ConsumablesResponse
import com.appcoins.product_inventory.model.ProductInfoResponse
import com.appcoins.product_inventory.model.PurchaseResponse
import com.appcoins.product_inventory.model.PurchaseStateResponse
import com.appcoins.product_inventory.model.PurchasesResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ProductInventoryRepositoryImpl @Inject constructor(
  private val productInventoryApi: ProductInventoryApi,
) : ProductInventoryRepository {

  override suspend fun isInAppBillingSupported(packageName: String): Boolean =
    productInventoryApi.isInAppBillingSupported(packageName)

  override suspend fun getConsumables(
    packageName: String,
    names: String,
  ): List<ProductInfoData> = productInventoryApi
    .getConsumables(packageName, names)
    .items
    .map(ProductInfoResponse::toProductInfoData)

  override suspend fun getProductInfo(
    name: String,
    sku: String?,
    currency: String?,
    country: String?,
  ): ProductInfoData = productInventoryApi.getProductInfo(
    name = name,
    sku = sku,
    currency = currency,
    country = country
  ).toProductInfoData()

  override suspend fun getPurchases(
    packageName: String,
    ewt: String,
  ): List<PurchaseInfoData> = productInventoryApi
    .getPurchases(
      packageName = packageName,
      authorization = "Bearer $ewt",
      type = "INAPP"
    )
    .items
    .map { it.toPurchaseInfoData(packageName) }

  internal interface ProductInventoryApi {

    @GET("productv2/8.20200301/applications/{domain}/inapp")
    suspend fun isInAppBillingSupported(@Path("domain") packageName: String): Boolean

    @GET("productv2/8.20200301/applications/{packageName}/inapp/consumables")
    suspend fun getConsumables(
      @Path("packageName") packageName: String,
      @Query("skus") names: String,
    ): ConsumablesResponse

    @GET("productv2/8.20200301/applications/{name}/inapp/consumables/{sku}")
    suspend fun getProductInfo(
      @Path("name") name: String,
      @Path("sku") sku: String? = null,
      @Query("currency") currency: String? = null,
      @Query("country") country: String? = null,
    ): ProductInfoResponse

    @GET("productv2/8.20200301/applications/{packageName}/inapp/consumable/purchases")
    suspend fun getPurchases(
      @Path("packageName") packageName: String,
      @Header("authorization") authorization: String,
      @Query("type") type: String,
      @Query("state") state: String = "PENDING",
      @Query("sku") sku: String? = null,
    ): PurchasesResponse
  }
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
