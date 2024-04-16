package com.appcoins.payments.products

import com.appcoins.payments.arch.ProductInfoData
import com.appcoins.payments.arch.PurchaseInfoData
import com.appcoins.payments.arch.PurchaseState
import com.appcoins.payments.arch.TransactionPrice
import com.appcoins.payments.json.jsonToBoolean
import com.appcoins.payments.network.HttpException
import com.appcoins.payments.network.RestClient
import com.appcoins.payments.network.get
import com.appcoins.payments.network.post
import com.appcoins.payments.products.error.ServerErrorException
import com.appcoins.payments.products.model.BillingSupport
import com.appcoins.payments.products.model.ProductInfoResponse
import com.appcoins.payments.products.model.PurchaseResponse
import com.appcoins.payments.products.model.PurchaseStateResponse
import com.appcoins.payments.products.model.jsonToConsumablesResponse
import com.appcoins.payments.products.model.jsonToProductInfoResponse
import com.appcoins.payments.products.model.jsonToPurchasesResponse
import java.net.UnknownHostException

internal class ProductsRepositoryImpl(
  private val restClient: RestClient,
) : ProductsRepository {

  override suspend fun isInAppBillingSupported(packageName: String) = runCatching {
    val result = restClient.get(
      path = "productv2/8.20230522/applications/$packageName/inapp"
    )?.jsonToBoolean() ?: false

    if (result) BillingSupport.SUPPORTED else BillingSupport.NOT_SUPPORTED
  }.getOrElse { exception ->
    when {
      exception is HttpException && exception.code in 500..599 -> BillingSupport.SERVER_ERROR
      exception is UnknownHostException -> BillingSupport.NO_INTERNET_CONNECTION
      else -> BillingSupport.UNKNOWN_ERROR
    }
  }

  override suspend fun getConsumables(
    packageName: String,
    names: String,
  ): List<ProductInfoData> = runCatching {
    restClient
      .get(
        path = "productv2/8.20230522/applications/$packageName/inapp/consumables",
        query = mapOf("skus" to names)
      )
      ?.jsonToConsumablesResponse()!!
      .items
      .map(ProductInfoResponse::toProductInfoData)
  }.getOrElse { throw mapException(it) }

  override suspend fun getProductInfo(
    name: String,
    sku: String?,
    currency: String?,
    country: String?,
  ): ProductInfoData = restClient.get(
    path = "productv2/8.20230522/applications/$name/inapp/consumables/$sku",
    query = mapOf(
      "currency" to currency,
      "country" to country
    ),
  )
    ?.jsonToProductInfoResponse()!!
    .toProductInfoData()

  override suspend fun getPurchases(
    packageName: String,
    ewt: String,
  ): List<PurchaseInfoData> = runCatching {
    restClient
      .get(
        path = "productv2/8.20230522/applications/$packageName/inapp/consumable/purchases",
        header = mapOf("authorization" to "Bearer $ewt"),
        query = mapOf(
          "type" to "INAPP",
          "state" to "PENDING",
          "sku" to null,
        ),
      )
      ?.jsonToPurchasesResponse()!!
      .items
      .map { it.toPurchaseInfoData(packageName) }
  }.getOrElse { throw mapException(it) }

  override suspend fun consumePurchase(
    domain: String,
    uid: String,
    authorization: String,
    payload: String?,
  ): Boolean = runCatching {
    restClient.post(
      path = "productv2/8.20230522/applications/$domain/inapp/purchases/$uid/consume",
      header = mapOf("authorization" to "Bearer $authorization"),
      query = mapOf("payload" to payload)
    ).let { true }
  }.getOrElse { throw mapException(it) }

  private fun mapException(exception: Throwable): Throwable {
    exception.printStackTrace()
    return when {
      exception is HttpException && exception.code in 500..599 -> ServerErrorException()
      else -> exception
    }
  }
}

interface ProductsRepository {

  /**
   * Check if billing is supported for the **packageName**.
   * @return [BillingSupport] type
   */
  @Throws(ServerErrorException::class)
  suspend fun isInAppBillingSupported(
    packageName: String,
  ): BillingSupport

  /**
   * Get the consumables for the **packageName** with the **names** separated by comma.
   * @return the list of consumables
   * @throws ServerErrorException when server returns an error between 500 and 599
   */
  @Throws(ServerErrorException::class)
  suspend fun getConsumables(
    packageName: String,
    names: String,
  ): List<ProductInfoData>

  /**
   * Get the product info from the **name**, **sku**, **currency** and **country**.
   * @return the product info data
   * @throws ServerErrorException when server returns an error between 500 and 599
   */
  @Throws(ServerErrorException::class)
  suspend fun getProductInfo(
    name: String,
    sku: String? = null,
    currency: String? = null,
    country: String? = null,
  ): ProductInfoData

  /**
   * Get the purchases from the given **packageName** and **ewt** authorization.
   * @return the list of product info data
   * @throws ServerErrorException when server returns an error between 500 and 599
   */
  @Throws(ServerErrorException::class)
  suspend fun getPurchases(
    packageName: String,
    ewt: String,
  ): List<PurchaseInfoData>

  /**
   * Consumes the purchase for the **domain** (package name), **uid** purchase token, **authorization** and **payload**.
   * @return true if it was consumed
   * @throws ServerErrorException when server returns an error between 500 and 599
   */
  @Throws(ServerErrorException::class)
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