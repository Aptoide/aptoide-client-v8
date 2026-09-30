package com.aptoide.android.aptoidegames.installer.gplay

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

/**
 * Fetches the Play Catalog Access delivery token from the Aptoide backend, which ingests
 * the Play catalog export and keeps the tokens fresh. An unknown app is not in the catalog;
 * any other failure (network error, timeout, a failing service) could not tell, and the two
 * are kept apart because only the first may hide an install.
 */
class AptoideCatalogTokenRepository @Inject constructor(
  private val playInlineConfigApi: PlayInlineConfigApi,
) : CatalogTokenRepository {

  // Passed to Play as-is: Google support confirmed catalog_token is a String extra
  // (their docs wrongly showed byte[] until 2026-08; a byte[] extra makes Finsky read
  // null and kill the half-sheet with a misleading "PITH: called from wrong URI" log)
  override suspend fun lookup(packageName: String): CatalogLookup = try {
    val token = withTimeout(TOKEN_FETCH_TIMEOUT_MILLIS) {
      playInlineConfigApi.getPlayInlineConfig(packageName)
    }.catalogToken
    // No token in the answer reads the same as no app: there is nothing to install it with
    token?.takeIf { it.isNotBlank() }?.let(CatalogLookup::Token) ?: CatalogLookup.NotInCatalog
  } catch (e: TimeoutCancellationException) {
    // A cancellation in name only: it is the fetch that gave up, not the caller
    Timber.tag(INLINE_INSTALL_TAG).d("$packageName: catalog token fetch timed out")
    CatalogLookup.Failed
  } catch (e: CancellationException) {
    throw e
  } catch (e: HttpException) {
    Timber.tag(INLINE_INSTALL_TAG).d("$packageName: catalog token fetch failed: $e")
    if (e.code() == NOT_FOUND) CatalogLookup.NotInCatalog else CatalogLookup.Failed
  } catch (e: Exception) {
    Timber.tag(INLINE_INSTALL_TAG).d("$packageName: catalog token fetch failed: $e")
    CatalogLookup.Failed
  }

  private companion object {
    const val INLINE_INSTALL_TAG = "InlineInstall"
    const val NOT_FOUND = 404

    // The fetch delays the reaction to the install button, so it fails fast
    // into the regular install path
    const val TOKEN_FETCH_TIMEOUT_MILLIS = 5_000L
  }
}
