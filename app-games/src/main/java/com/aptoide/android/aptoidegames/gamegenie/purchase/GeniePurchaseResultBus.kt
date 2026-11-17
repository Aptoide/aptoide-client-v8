package com.aptoide.android.aptoidegames.gamegenie.purchase

import android.net.Uri
import com.aptoide.android.aptoidegames.BuildConfig
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * HACKATHON: carries the game's purchase-result callback from MainActivity (deep link) to the
 * GameGenieViewModel that is waiting for it. Results whose ref nobody waits for are dropped.
 */
@Singleton
class GeniePurchaseResultBus @Inject constructor() {

  private val _results = MutableSharedFlow<GeniePurchaseCallback>(extraBufferCapacity = 8)
  val results: SharedFlow<GeniePurchaseCallback> = _results.asSharedFlow()

  fun post(callback: GeniePurchaseCallback) {
    _results.tryEmit(callback)
  }
}

data class GeniePurchaseCallback(
  val ref: String,
  val status: PurchaseStatus?,
  val rawStatus: String?,
  val sku: String?,
  val orderId: String?,
)

/**
 * Callback contract (frozen, shared with the game):
 * `<scheme>genie/purchase-result?ref=<uuid>&status=<...>&sku=<sku>&order_id=<id, optional>`
 */
object GeniePurchaseContract {
  const val CALLBACK_HOST = "genie"
  const val CALLBACK_PATH = "/purchase-result"

  /** e.g. `ag://genie/purchase-result` for Aptoide Games. */
  val callbackBase: String = "${BuildConfig.DEEP_LINK_SCHEMA}$CALLBACK_HOST$CALLBACK_PATH"

  /** [offersSession]: adds `session=1`, telling the SDK it may redeem a wallet session grant. */
  fun buildPurchaseUri(sku: String, type: String, ref: String, offersSession: Boolean = false): Uri =
    Uri.Builder()
      .scheme("aptoide-iap")
      .authority("purchase")
      .appendQueryParameter("v", "1")
      .appendQueryParameter("sku", sku)
      .appendQueryParameter("type", type)
      .appendQueryParameter("ref", ref)
      .appendQueryParameter("source", "gamegenie")
      .appendQueryParameter("callback", callbackBase)
      .apply { if (offersSession) appendQueryParameter("session", "1") }
      .build()

  fun parseCallback(uri: Uri?): GeniePurchaseCallback? {
    uri ?: return null
    val scheme = BuildConfig.DEEP_LINK_SCHEMA.substringBefore("://")
    if (!uri.scheme.equals(scheme, ignoreCase = true)) return null
    if (uri.host != CALLBACK_HOST || uri.path != CALLBACK_PATH) return null
    val ref = runCatching { uri.getQueryParameter("ref") }.getOrNull()
      ?.takeIf { it.isNotBlank() } ?: return null
    val rawStatus = uri.getQueryParameter("status")
    return GeniePurchaseCallback(
      ref = ref,
      status = PurchaseStatus.fromWire(rawStatus),
      rawStatus = rawStatus,
      sku = uri.getQueryParameter("sku"),
      orderId = uri.getQueryParameter("order_id"),
    )
  }
}
