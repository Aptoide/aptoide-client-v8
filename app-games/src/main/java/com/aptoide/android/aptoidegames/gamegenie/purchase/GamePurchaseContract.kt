package com.aptoide.android.aptoidegames.gamegenie.purchase

import android.net.Uri
import com.aptoide.android.aptoidegames.BuildConfig

/** The game's answer to a purchase link, parsed from the callback deep link. */
data class GamePurchaseCallback(
  val target: PurchaseCallbackTarget,
  val ref: String,
  val status: PurchaseStatus?,
  val rawStatus: String?,
  val sku: String?,
  val orderId: String?,
)

/**
 * Where a purchase started, and so where the game's callback returns to. The billing SDK only
 * opens callbacks whose scheme is `ag` or `aptoide`; host and path are ours to choose.
 */
enum class PurchaseCallbackTarget(val host: String, val source: String) {
  /** `ag://genie/purchase-result`: purchases started from the Genie chat. */
  GENIE("genie", "gamegenie"),

  /** `ag://shop/purchase-result`: purchases started from the AppView Shop tab. */
  SHOP("shop", "appview");

  val callbackBase: String
    get() = "${BuildConfig.DEEP_LINK_SCHEMA}$host${GamePurchaseContract.CALLBACK_PATH}"
}

/**
 * Purchase link contract (frozen, shared with the billing SDK):
 * `aptoide-iap://purchase?v=1&sku=&type=&ref=&source=&callback=[&session=1]`
 * and its callback `<callback>?ref=<uuid>&status=<...>&sku=<sku>&order_id=<id, optional>`.
 */
object GamePurchaseContract {
  const val CALLBACK_PATH = "/purchase-result"

  /** [offersSession]: adds `session=1`, telling the SDK it may redeem a wallet session grant. */
  fun buildPurchaseUri(
    sku: String,
    type: String,
    ref: String,
    target: PurchaseCallbackTarget,
    offersSession: Boolean = false,
  ): Uri =
    Uri.Builder()
      .scheme("aptoide-iap")
      .authority("purchase")
      .appendQueryParameter("v", "1")
      .appendQueryParameter("sku", sku)
      .appendQueryParameter("type", type)
      .appendQueryParameter("ref", ref)
      .appendQueryParameter("source", target.source)
      .appendQueryParameter("callback", target.callbackBase)
      .apply { if (offersSession) appendQueryParameter("session", "1") }
      .build()

  fun parseCallback(uri: Uri?): GamePurchaseCallback? {
    uri ?: return null
    val scheme = BuildConfig.DEEP_LINK_SCHEMA.substringBefore("://")
    if (!uri.scheme.equals(scheme, ignoreCase = true)) return null
    if (uri.path != CALLBACK_PATH) return null
    val target = PurchaseCallbackTarget.entries.firstOrNull { it.host == uri.host } ?: return null
    val ref = runCatching { uri.getQueryParameter("ref") }.getOrNull()
      ?.takeIf { it.isNotBlank() } ?: return null
    val rawStatus = uri.getQueryParameter("status")
    return GamePurchaseCallback(
      target = target,
      ref = ref,
      status = PurchaseStatus.fromWire(rawStatus),
      rawStatus = rawStatus,
      sku = uri.getQueryParameter("sku"),
      orderId = uri.getQueryParameter("order_id"),
    )
  }
}

/**
 * The plain-language outcome of a purchase that did not succeed. [retryAction] is the label of the
 * button that starts it again ("Pay" in Genie, "Buy" in the Shop tab).
 */
fun purchaseNotCompletedMessage(
  status: PurchaseStatus?,
  gameName: String,
  itemTitle: String,
  retryAction: String,
): String = when (status) {
  PurchaseStatus.CANCELED ->
    "No problem, the purchase was canceled. You weren't charged."

  PurchaseStatus.LOGIN_REQUIRED ->
    "$gameName needs you signed in before buying. Sign in there and tap $retryAction again."

  PurchaseStatus.UNKNOWN_SKU ->
    "$gameName doesn't sell $itemTitle right now, so nothing was charged."

  PurchaseStatus.FAILED, PurchaseStatus.SUCCESS, null ->
    // The store only knows the game reported a failure, not whether money moved.
    "$gameName couldn't complete the purchase. If you were charged, it shows in " +
      "your wallet's transactions. You can tap $retryAction to try again."
}
