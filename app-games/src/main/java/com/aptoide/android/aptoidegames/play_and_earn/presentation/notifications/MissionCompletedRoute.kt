package com.aptoide.android.aptoidegames.play_and_earn.presentation.notifications

import cm.aptoide.pt.feature_apps.domain.AppSource
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute

/**
 * Where tapping the mission-completed notification lands: the appview of the game that was being
 * played, gamified so it opens straight on the Rewards tab. Resolved by package name with no store,
 * like every other Play & Earn entry point (AND-719: a Play & Earn game may not be in the brand's
 * store).
 *
 * The source has to stay a single path segment: the appview destination is `app/{source}`, so a
 * `/store_name=…` suffix makes the route unmatchable and the navigation throws (AND-914).
 */
fun missionCompletedRoute(packageName: String): String = buildAppViewRoute(
  appSource = AppSource.of(appId = null, packageName = packageName),
  isGamified = true
)
