package com.aptoide.android.aptoidegames.installer

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * Tells the UI whether an app installs via Google Play (Play Catalog inline install), so
 * install surfaces can show the required "Google Play" attribution before the user taps
 * Install, and whether it can be installed at all. Bound only in Play-distributed builds —
 * everywhere else the Optional is empty and no attribution is ever shown.
 */
interface PlayCatalogChecker {

  /**
   * Emits what is known about [packageName]'s presence in the catalog. Cache-only: never
   * triggers network, so list cards can observe it freely.
   */
  fun observeCatalogStatus(packageName: String): Flow<CatalogStatus>

  /** Emits whether [packageName] installs via Google Play. Cache-only, as above. */
  fun observeIsPlayCatalog(packageName: String): Flow<Boolean> =
    observeCatalogStatus(packageName).map { it == CatalogStatus.IN_CATALOG }.distinctUntilChanged()

  /** Fire-and-forget, deduped catalog lookup so install surfaces can label before the tap. */
  fun prefetch(packageName: String)
}
