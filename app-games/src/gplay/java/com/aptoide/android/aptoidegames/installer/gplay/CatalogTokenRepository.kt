package com.aptoide.android.aptoidegames.installer.gplay

import timber.log.Timber
import javax.inject.Inject

/**
 * Provides the Play Catalog Access delivery token required by Google Play inline installs.
 * Tokens are short-lived and validated by Play for freshness, target package and caller,
 * so they must be fetched at install time, not cached long-term.
 */
interface CatalogTokenRepository {

  /**
   * Returns a fresh catalog token for [packageName], or null if none is available.
   * A String, passed to Play as-is - see [AptoideCatalogTokenRepository].
   */
  suspend fun getCatalogToken(packageName: String): String?
}

/**
 * Test-only implementation that always returns a fake token, exercising the inline flow
 * (half-sheet launch, UI state, notification, cancel and retry-fallback) without the
 * backend — Play is expected to reject the token, which is also the way to test the abort
 * path. NEVER bind this in a shipped build; swap it in locally via
 * [com.aptoide.android.aptoidegames.installer.gplay.di.InlineInstallModule].
 */
class FakeCatalogTokenRepository @Inject constructor() : CatalogTokenRepository {

  override suspend fun getCatalogToken(packageName: String): String? {
    Timber.tag("InlineInstall").d("$packageName: using FAKE catalog token")
    return "debug-fake-catalog-token"
  }
}
