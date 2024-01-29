package com.appcoins.product_inventory

import com.appcoins.product_inventory.domain.ProductInfoData
import com.appcoins.product_inventory.model.ConsumablesResponse
import com.appcoins.product_inventory.model.ProductInfoResponse
import retrofit2.http.GET
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
}

private fun ProductInfoResponse.toProductInfoData() = ProductInfoData(
  sku = this.sku,
  title = this.title,
  description = this.description,
  priceValue = this.price.value,
  priceCurrency = this.price.currency,
  priceInDollars = this.price.usd.value
)
