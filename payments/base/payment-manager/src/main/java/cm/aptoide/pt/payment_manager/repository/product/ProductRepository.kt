package cm.aptoide.pt.payment_manager.repository.product

import cm.aptoide.pt.payment_manager.repository.product.domain.ProductInfoData
import cm.aptoide.pt.payment_manager.repository.product.model.ProductInfoResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ProductRepositoryImpl @Inject constructor(
  private val productApi: ProductApi,
) : ProductRepository {

  override suspend fun getProductInfo(
    name: String,
    sku: String?,
    currency: String?,
    country: String?,
  ): ProductInfoData {
    val productInfo = productApi.getProductInfo(
      name = name,
      sku = sku,
      currency = currency,
      country = country
    )
    return ProductInfoData(
      sku = productInfo.sku,
      title = productInfo.title,
      description = productInfo.description,
      priceValue = productInfo.price.value,
      priceCurrency = productInfo.price.currency,
      priceInDollars = productInfo.price.usd.value
    )
  }

  internal interface ProductApi {

    @GET("productv2/8.20200301/applications/{name}/inapp/consumables/{sku}")
    suspend fun getProductInfo(
      @Path("name") name: String,
      @Path("sku") sku: String? = null,
      @Query("currency") currency: String? = null,
      @Query("country") country: String? = null,
    ): ProductInfoResponse
  }
}

interface ProductRepository {

  suspend fun getProductInfo(
    name: String,
    sku: String? = null,
    currency: String? = null,
    country: String? = null,
  ): ProductInfoData
}
