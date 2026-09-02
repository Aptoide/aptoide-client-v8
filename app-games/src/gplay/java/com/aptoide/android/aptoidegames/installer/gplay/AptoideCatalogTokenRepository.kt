package com.aptoide.android.aptoidegames.installer.gplay

import kotlinx.coroutines.withTimeout
import timber.log.Timber
import javax.inject.Inject

/**
 * Fetches the Play Catalog Access delivery token from the Aptoide backend, which ingests
 * the Play catalog export and keeps the tokens fresh. Any failure (app not in the catalog,
 * network error, timeout) returns null, sending the install through the regular path.
 */
class AptoideCatalogTokenRepository @Inject constructor(
  private val playInlineConfigApi: PlayInlineConfigApi,
) : CatalogTokenRepository {

  // Passed to Play as-is: Google support confirmed catalog_token is a String extra
  // (their docs wrongly showed byte[] until 2026-08; a byte[] extra makes Finsky read
  // null and kill the half-sheet with a misleading "PITH: called from wrong URI" log)
  override suspend fun getCatalogToken(packageName: String): String? = runCatching {
    withTimeout(TOKEN_FETCH_TIMEOUT_MILLIS) {
      playInlineConfigApi.getPlayInlineConfig(packageName)
    }
  }
    .onFailure {
      Timber.tag(INLINE_INSTALL_TAG).d("$packageName: catalog token fetch failed: $it")
    }
    .getOrNull()
    ?.catalogToken
    ?.takeIf { it.isNotBlank() }

  private companion object {
    const val INLINE_INSTALL_TAG = "InlineInstall"

    // The fetch delays the reaction to the install button, so it fails fast
    // into the regular install path
    const val TOKEN_FETCH_TIMEOUT_MILLIS = 5_000L
  }
}
