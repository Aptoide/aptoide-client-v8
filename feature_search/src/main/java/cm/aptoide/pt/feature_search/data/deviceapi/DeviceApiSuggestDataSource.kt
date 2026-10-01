package cm.aptoide.pt.feature_search.data.deviceapi

import cm.aptoide.pt.device_api.error.deviceApiCall
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/** Reads search suggestions from the device API, within the catalog [variant]. */
class DeviceApiSuggestDataSource(
  private val service: DeviceApiSuggestService,
  private val variant: String,
  private val dispatcher: CoroutineDispatcher,
) {

  /**
   * The terms suggested for [query]. Suggestions are a convenience, so failing to read them
   * reads as having none instead of getting in the way of the search.
   */
  suspend fun suggest(query: String): List<String> = withContext(dispatcher) {
    try {
      deviceApiCall { service.suggest(q = query, limit = null, variant = variant) }
        .suggestions.orEmpty()
        .mapNotNull { suggestion -> suggestion.term?.takeIf { it.isNotBlank() } }
    } catch (e: CancellationException) {
      throw e
    } catch (_: Exception) {
      emptyList()
    }
  }
}
