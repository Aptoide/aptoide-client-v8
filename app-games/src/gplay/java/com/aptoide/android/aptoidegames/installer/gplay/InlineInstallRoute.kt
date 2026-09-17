package com.aptoide.android.aptoidegames.installer.gplay

import cm.aptoide.pt.feature_apps.data.App
import com.aptoide.android.aptoidegames.installer.excludedFromPlayCatalog

/**
 * Where an install goes, decided from the [App] alone before any catalog token is fetched.
 * Only [PLAY_CATALOG] may reach the token repository; the other three are settled without a
 * request, which is what keeps BDS apps from ever triggering one.
 */
internal enum class InlineInstallRoute {
  /** Roblox / Free Fire: Play's public details overlay, token-less, never Aptoide. */
  DETAILS_OVERLAY,

  /** BDS (Catappult) build: Aptoide's own installer, Play catalog not consulted. */
  APTOIDE_ONLY,

  /** An earlier inline attempt this session was rejected without UI: regular install path. */
  ABORTED,

  /** A regular catalog app: fetch the token and, if there is one, divert to Play. */
  PLAY_CATALOG,
}

/**
 * The resolver's pre-token ladder as a pure function, so its ordering is a tested contract.
 * The overlay rule comes first on purpose and matches [excludedFromPlayCatalog] yielding to the
 * same titles: whichever gate asks, Roblox and Free Fire end up on the overlay, labelled.
 */
internal fun inlineInstallRoute(app: App, aborted: Set<String>): InlineInstallRoute = when {
  app.installsThroughDetailsOverlay() -> InlineInstallRoute.DETAILS_OVERLAY
  app.excludedFromPlayCatalog() -> InlineInstallRoute.APTOIDE_ONLY
  app.packageName in aborted -> InlineInstallRoute.ABORTED
  else -> InlineInstallRoute.PLAY_CATALOG
}
