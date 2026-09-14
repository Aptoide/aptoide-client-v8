package com.aptoide.android.aptoidegames.installer.gplay

import com.aptoide.android.aptoidegames.apkfy.isFreeFirePackage
import com.aptoide.android.aptoidegames.apkfy.isRobloxPackage
import com.aptoide.android.aptoidegames.installer.PlayCatalogChecker
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.concurrent.ConcurrentHashMap

/**
 * Caches catalog lookups on top of [AptoideCatalogTokenRepository] so install surfaces can
 * label Play Catalog apps before the user taps Install ([PlayCatalogChecker]), and so the
 * click path reuses a recently fetched token instead of paying the fetch latency again.
 *
 * Negative results (not in the catalog, or fetch failure) are cached for labeling only:
 * the click path ignores them and refetches, keeping install behavior identical to the
 * uncached repository - a transient prefetch failure must never divert an inline install.
 */
class CachingCatalogTokenRepository(
  private val origin: CatalogTokenRepository,
  private val scope: CoroutineScope,
  private val now: () -> Long,
) : CatalogTokenRepository, PlayCatalogChecker {

  private data class Entry(val token: String?, val fetchedAt: Long)

  private val entries = MutableStateFlow<Map<String, Entry>>(emptyMap())
  private val inFlight = ConcurrentHashMap<String, Deferred<String?>>()

  override fun observeIsPlayCatalog(packageName: String): Flow<Boolean> =
    if (installsThroughPlayOverlay(packageName)) {
      flowOf(true)
    } else {
      entries.map { it[packageName]?.token != null }.distinctUntilChanged()
    }

  override fun prefetch(packageName: String) {
    if (installsThroughPlayOverlay(packageName)) return
    // Stale entries (expired token or an old negative result) are refetched, so a
    // transient failure cannot suppress the mandatory attribution for the process lifetime
    val cached = entries.value[packageName]
    if (cached != null && now() - cached.fetchedAt < TOKEN_REUSE_TTL_MILLIS) return
    fetchAsync(packageName)
  }

  override suspend fun getCatalogToken(packageName: String): String? {
    val cached = entries.value[packageName]
    if (cached?.token != null && now() - cached.fetchedAt < TOKEN_REUSE_TTL_MILLIS) {
      return cached.token
    }
    return fetchAsync(packageName).await()
  }

  private fun fetchAsync(packageName: String): Deferred<String?> =
    inFlight.computeIfAbsent(packageName) {
      scope.async {
        try {
          // The origin is expected to map failures to null, but an install click must
          // never crash on a throwing origin - it falls back to the regular path instead
          val token = try {
            origin.getCatalogToken(packageName)
          } catch (e: CancellationException) {
            throw e
          } catch (e: Exception) {
            null
          }
          entries.update { it + (packageName to Entry(token, now())) }
          token
        } finally {
          inFlight.remove(packageName)
        }
      }
    }

  // Overlay-only titles install through the Play details overlay - via Google Play by
  // definition, no catalog token involved
  private fun installsThroughPlayOverlay(packageName: String): Boolean =
    isRobloxPackage(packageName) || isFreeFirePackage(packageName)

  companion object {

    // Tokens stay valid for days (backend re-ingests before the 7 day validity runs out),
    // so a short reuse window is safe and makes the install click instant after a prefetch
    const val TOKEN_REUSE_TTL_MILLIS = 5 * 60 * 1_000L
  }
}
