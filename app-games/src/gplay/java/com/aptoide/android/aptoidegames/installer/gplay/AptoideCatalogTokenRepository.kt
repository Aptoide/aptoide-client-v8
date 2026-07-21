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

  override suspend fun getCatalogToken(packageName: String): ByteArray? = runCatching {
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
    // TODO(inline-installs): confirm with a real export token whether the backend serves it
    //  Base64-encoded (protobuf bytes) and needs decoding before being passed to Play
    ?.toByteArray()

  private companion object {
    const val INLINE_INSTALL_TAG = "InlineInstall"

    // The fetch delays the reaction to the install button, so it fails fast
    // into the regular install path
    const val TOKEN_FETCH_TIMEOUT_MILLIS = 5_000L
  }
}
