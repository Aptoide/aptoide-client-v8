package com.aptoide.android.aptoidegames.installer.gplay

import timber.log.Timber
import javax.inject.Inject

/** The answer of a catalog lookup. */
sealed interface CatalogLookup {
  /** A fresh token, a String passed to Play as-is - see [AptoideCatalogTokenRepository]. */
  data class Token(val value: String) : CatalogLookup

  /** The catalog does not hold the app, so Play cannot install it. */
  data object NotInCatalog : CatalogLookup

  /** The lookup could not tell: no connection, a timeout, or a failing service. */
  data object Failed : CatalogLookup
}

/**
 * Provides the Play Catalog Access delivery token required by Google Play inline installs.
 * Tokens are short-lived and validated by Play for freshness, target package and caller,
 * so they must be fetched at (or shortly before) install time, not cached long-term - see
 * [CachingCatalogTokenRepository.TOKEN_REUSE_TTL_MILLIS] for the allowed reuse window.
 */
interface CatalogTokenRepository {

  /** Looks the catalog up for [packageName]. */
  suspend fun lookup(packageName: String): CatalogLookup

  /** Returns a fresh catalog token for [packageName], or null if none is available. */
  suspend fun getCatalogToken(packageName: String): String? =
    (lookup(packageName) as? CatalogLookup.Token)?.value
}

/**
 * Test-only implementation that always returns a fake token, exercising the inline flow
 * (half-sheet launch, UI state, notification, cancel and retry-fallback) without the
 * backend — Play is expected to reject the token, which is also the way to test the abort
 * path. Also makes every app label as Play catalog, exercising the "Google Play"
 * attribution on all install surfaces. NEVER bind this in a shipped build; swap it in as
 * the cache origin locally via
 * [com.aptoide.android.aptoidegames.installer.gplay.di.InlineInstallModule].
 */
class FakeCatalogTokenRepository @Inject constructor() : CatalogTokenRepository {

  override suspend fun lookup(packageName: String): CatalogLookup {
    Timber.tag("InlineInstall").d("$packageName: using FAKE catalog token")
    return CatalogLookup.Token("debug-fake-catalog-token")
  }
}
