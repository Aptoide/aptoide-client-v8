package com.aptoide.android.aptoidegames.installer

import kotlinx.coroutines.flow.Flow

/**
 * Tells the UI whether an app installs via Google Play (Play Catalog inline install), so
 * install surfaces can show the required "Google Play" attribution before the user taps
 * Install. Bound only in Play-distributed builds — everywhere else the Optional is empty
 * and no attribution is ever shown.
 */
interface PlayCatalogChecker {

  /**
   * Emits whether [packageName] installs via Google Play. Cache-only: never triggers
   * network, so list cards can observe it freely.
   */
  fun observeIsPlayCatalog(packageName: String): Flow<Boolean>

  /** Fire-and-forget, deduped catalog lookup so install surfaces can label before the tap. */
  fun prefetch(packageName: String)
}
