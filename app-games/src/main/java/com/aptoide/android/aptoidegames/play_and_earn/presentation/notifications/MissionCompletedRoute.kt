package com.aptoide.android.aptoidegames.play_and_earn.presentation.notifications

import cm.aptoide.pt.feature_apps.domain.AppSource
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.play_and_earn.domain.sessions.CompletedMissionEvent
import com.aptoide.android.aptoidegames.play_and_earn.presentation.progress.myProgressRoute

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

/**
 * A game's mission opens the game's Rewards tab; one of the player's own (Hackathon 2026) opens
 * My progress, where those missions are listed.
 */
fun completedMissionRoute(event: CompletedMissionEvent): String =
  if (event.isUserMission) myProgressRoute else missionCompletedRoute(event.packageName)
