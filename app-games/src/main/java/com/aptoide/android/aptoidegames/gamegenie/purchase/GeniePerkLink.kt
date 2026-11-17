package com.aptoide.android.aptoidegames.gamegenie.purchase

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.URI
import java.net.URLDecoder
import javax.inject.Inject
import javax.inject.Singleton

/**
 * HACKATHON: "buy this item in Genie" deep link, the tap target of the perk push notification:
 * `ag://genie/buy?package=<package>&sku=<sku>&perk=<display text>`
 *
 * A real push only needs `dti.link` set to this URI (the existing deep-link push pipeline).
 */
data class GeniePerkRequest(
  val item: GeniePurchaseItem,
  /** HACKATHON MOCK: display-only perk text; no perk/bonus backend is queried or applied. */
  val perk: String?,
  /** True when the link says it comes from an outside AI assistant (`via=assistant`). */
  val fromAssistant: Boolean = false,
)

object GeniePerkLink {
  const val HOST = "genie"
  const val PATH = "/buy"
  private const val MAX_PERK_LENGTH = 120
  private val controlChars = Regex("\\p{Cntrl}")

  fun build(scheme: String, packageName: String, sku: String, perk: String?): String =
    buildString {
      append(scheme).append(HOST).append(PATH)
      append("?package=").append(encode(packageName))
      append("&sku=").append(encode(sku))
      perk?.let { append("&perk=").append(encode(it)) }
    }

  /**
   * Parses [link] (scheme as in `ag://`, e.g. [deepLinkScheme] = "ag"). Package and sku are only
   * accepted when they name a catalogue item: nothing else from the link is trusted.
   */
  fun parse(
    link: String?,
    deepLinkScheme: String,
    catalog: List<GeniePurchaseItem> = GeniePurchaseCatalog.items,
  ): GeniePerkRequest? {
    val uri = link?.let { runCatching { URI(it.trim()) }.getOrNull() } ?: return null
    if (!uri.scheme.equals(deepLinkScheme, ignoreCase = true)) return null
    if (uri.host != HOST || uri.path != PATH) return null
    val params = parseQuery(uri.rawQuery)
    val packageName = params["package"] ?: return null
    val sku = params["sku"] ?: return null
    val item = catalog.firstOrNull { packageName in it.packageCandidates && it.sku == sku }
      ?: return null
    val perk = params["perk"]
      ?.replace(controlChars, " ")
      ?.trim()
      ?.take(MAX_PERK_LENGTH)
      ?.takeIf { it.isNotEmpty() }
    return GeniePerkRequest(
      item = item,
      perk = perk,
      fromAssistant = params["via"].equals("assistant", ignoreCase = true),
    )
  }

  /**
   * HACKATHON: the ChatGPT plugin's https bridge link (`https://<host>.trycloudflare.com/go?…`).
   * The debug build claims it as an app link, so Buy in ChatGPT opens Genie directly, skipping
   * the bridge page. Maps it to the `ag://genie/buy` form; package and sku are still checked
   * against the catalogue by [parse].
   */
  fun fromAssistantBridge(link: String?, scheme: String): String? {
    val uri = link?.let { runCatching { URI(it.trim()) }.getOrNull() } ?: return null
    if (uri.scheme != "https" || uri.path != "/go") return null
    if (uri.host?.endsWith(".trycloudflare.com") != true) return null
    return "$scheme://$HOST$PATH?${uri.rawQuery.orEmpty()}"
  }

  /** The first Genie line of a perk flow. */
  fun introMessage(request: GeniePerkRequest): String = if (request.fromAssistant) {
    // For an assistant hand-off the text is the assistant's reason, not a perk.
    val reason = request.perk?.let { " ($it)" }.orEmpty()
    "Your assistant picked ${request.item.title} in ${request.item.gameName}$reason. " +
      "Let's get it for you."
  } else request.perk
    ?.let { "${request.item.gameName} has a perk for you today: $it. Let's get it for you." }
    ?: "Let's get ${request.item.title} in ${request.item.gameName} for you."

  private fun parseQuery(rawQuery: String?): Map<String, String> {
    val result = LinkedHashMap<String, String>()
    rawQuery.orEmpty().split('&').filter { it.isNotEmpty() }.forEach { pair ->
      val index = pair.indexOf('=')
      val key = decode(if (index >= 0) pair.substring(0, index) else pair)
      val value = decode(if (index >= 0) pair.substring(index + 1) else "")
      // First occurrence wins so a duplicated parameter cannot override another one.
      if (key != null && value != null && key !in result) result[key] = value
    }
    return result
  }

  private fun decode(value: String): String? =
    runCatching { URLDecoder.decode(value, "UTF-8") }.getOrNull()

  private fun encode(value: String): String =
    java.net.URLEncoder.encode(value, "UTF-8").replace("+", "%20")
}

/**
 * HACKATHON: hands a perk request from MainActivity (notification tap / deep link) to the Genie
 * screen, which may not exist yet (cold start). Holds at most one request until consumed.
 */
@Singleton
class GeniePerkRequests @Inject constructor() {
  private val _pending = MutableStateFlow<GeniePerkRequest?>(null)
  val pending: StateFlow<GeniePerkRequest?> = _pending.asStateFlow()

  fun post(request: GeniePerkRequest) {
    _pending.value = request
  }

  /** Takes [request] if it is still the pending one. */
  fun consume(request: GeniePerkRequest): Boolean =
    _pending.compareAndSet(request, null)
}
