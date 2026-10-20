package com.aptoide.android.aptoidegames.play_and_earn.presentation.notifications

import cm.aptoide.pt.feature_apps.domain.AppSource
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute

/**
 * Where tapping the mission-completed notification lands: the appview of the game that was being
 * played, gamified so it opens straight on the Rewards tab. Resolved by package name with no store:
 * the other Play & Earn entry points carry no store either (AND-719: a Play & Earn game may not be
 * in the brand's store), most of them resolving by app id.
 *
 * The source has to stay a single path segment: the appview destination is `app/{source}`, so a
 * `/store_name=…` suffix makes the route unmatchable and the navigation throws (AND-914).
 */
fun missionCompletedRoute(packageName: String): String = buildAppViewRoute(
  appSource = AppSource.of(appId = null, packageName = packageName),
  isGamified = true
)
