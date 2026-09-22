package com.aptoide.android.aptoidegames.play_and_earn.presentation.notifications

import cm.aptoide.pt.feature_apps.domain.AppSource
import cm.aptoide.pt.feature_apps.domain.AppSource.Companion.appendIfRequired
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute

/**
 * Where tapping the mission-completed notification lands: the appview of the game that was being
 * played, gamified so it opens straight on the Rewards tab — the same destination every other
 * Play & Earn entry point uses.
 */
fun missionCompletedRoute(packageName: String): String = buildAppViewRoute(
  appSource = MissionAppSource(packageName),
  isGamified = true
)

/**
 * A mission only knows the package it was played in, never an app id. The appview skips its own
 * `store_name` append for gamified routes — harmless for the other Play & Earn entry points, which
 * all resolve by app id, but it would leave this route as the one package-name appview link in the
 * app without a store, resolving against the backend default instead of the brand's own.
 */
private class MissionAppSource(override val packageName: String) : AppSource {
  override fun asSource(): String = super.asSource().appendIfRequired(BuildConfig.MARKET_NAME)
}
