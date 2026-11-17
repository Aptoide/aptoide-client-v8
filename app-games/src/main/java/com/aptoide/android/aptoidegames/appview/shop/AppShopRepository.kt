package com.aptoide.android.aptoidegames.appview.shop

import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.gamegenie.purchase.GeniePurchaseCatalog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONObject
import timber.log.Timber
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

/** One in-app product a game sells, as shown on the AppView Shop tab. */
data class ShopProduct(
  val sku: String,
  val type: String,
  val title: String,
  val description: String?,
  val priceLabel: String,
  val priceMicros: Long?,
)

data class ShopProducts(
  val products: List<ShopProduct>,
  /** True when the list comes from the hardcoded demo catalogue, not the product API. */
  val fromDemoCatalogue: Boolean,
)

/**
 * HACKATHON: the game's in-app consumables, read from the same public product endpoint the
 * billing SDK uses for its SKU details (`productv2 .../inapp/consumables`, no auth needed).
 * Falls back to the Genie demo catalogue when the call fails or returns nothing.
 */
@Singleton
class AppShopRepository @Inject constructor() {

  suspend fun getProducts(packageName: String): ShopProducts {
    val remote = withTimeoutOrNull(TIMEOUT_MS) {
      withContext(Dispatchers.IO) {
        runCatching { fetchConsumables(packageName) }
          .onFailure { Timber.w(it, "Shop: no products from the product API for $packageName") }
          .getOrNull()
      }
    }
    if (!remote.isNullOrEmpty()) {
      return ShopProducts(orderForShop(remote, packageName), fromDemoCatalogue = false)
    }
    return ShopProducts(catalogueProducts(packageName), fromDemoCatalogue = true)
  }

  private fun fetchConsumables(packageName: String): List<ShopProduct> {
    val url = URL(
      "$productApiHost/productv2/8.20240901/applications/" +
        "${URLEncoder.encode(packageName, "UTF-8")}/inapp/consumables"
    )
    val connection = (url.openConnection() as HttpURLConnection).apply {
      connectTimeout = TIMEOUT_MS.toInt()
      readTimeout = TIMEOUT_MS.toInt()
      setRequestProperty("Accept", "application/json")
    }
    try {
      if (connection.responseCode !in 200..299) return emptyList()
      val body = connection.inputStream.bufferedReader().use { it.readText() }
      val items = JSONObject(body).optJSONArray("items") ?: return emptyList()
      return (0 until items.length()).mapNotNull { index ->
        val item = items.optJSONObject(index) ?: return@mapNotNull null
        val price = item.optJSONObject("price") ?: return@mapNotNull null
        ShopProduct(
          sku = item.optString("sku").takeIf { it.isNotBlank() } ?: return@mapNotNull null,
          type = "inapp",
          title = item.optString("title").takeIf { it.isNotBlank() } ?: item.optString("sku"),
          description = item.optString("description")
            .takeIf { it.isNotBlank() && it != "null" },
          priceLabel = price.optString("label").takeIf { it.isNotBlank() }
            ?: return@mapNotNull null,
          priceMicros = price.optLong("micros").takeIf { price.has("micros") },
        )
      }
    } finally {
      connection.disconnect()
    }
  }

  private fun catalogueProducts(packageName: String): List<ShopProduct> =
    GeniePurchaseCatalog.items
      .filter { packageName in it.packageCandidates }
      .map {
        ShopProduct(
          sku = it.sku,
          type = it.type,
          title = it.title,
          description = null,
          priceLabel = it.price,
          priceMicros = null,
        )
      }

  companion object {
    private const val TIMEOUT_MS = 3_000L

    /** Same hosts the billing SDK uses (dev builds talk to the dev backend). */
    private val productApiHost: String
      get() = if (BuildConfig.FLAVOR_mode == "dev") {
        "https://api.dev.catappult.io"
      } else {
        "https://api.catappult.io"
      }

    // HACKATHON (demo curation): the dev catalogue has test SKUs with absurd prices (e.g. €314.90).
    // Show the demo catalogue item first, then at most the 3 cheapest others, none above €50.
    private const val MAX_OTHER_PRODUCTS = 3
    private const val MAX_PRICE_MICROS = 50_000_000L

    fun orderForShop(products: List<ShopProduct>, packageName: String): List<ShopProduct> {
      val featuredSkus = GeniePurchaseCatalog.items
        .filter { packageName in it.packageCandidates }
        .map { it.sku }
        .toSet()
      val (featured, others) = products.partition { it.sku in featuredSkus }
      return featured + others
        .filter { (it.priceMicros ?: 0L) <= MAX_PRICE_MICROS }
        .sortedBy { it.priceMicros ?: Long.MAX_VALUE }
        .take(MAX_OTHER_PRODUCTS)
    }
  }
}
