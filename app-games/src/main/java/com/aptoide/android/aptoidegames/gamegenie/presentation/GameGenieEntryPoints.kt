package com.aptoide.android.aptoidegames.gamegenie.presentation

import androidx.compose.runtime.Composable
import cm.aptoide.pt.feature_search.utils.isValidSearch
import com.aptoide.android.aptoidegames.analytics.dto.BundleMeta
import com.aptoide.android.aptoidegames.analytics.presentation.withBundleMeta
import com.aptoide.android.aptoidegames.gamegenie.analytics.GameGenieEntryPoint
import com.aptoide.android.aptoidegames.gamegenie.analytics.rememberGameGenieAnalytics

/**
 * Remote flag for the GameGenie entry points in search and Settings (APT-247). It is a different
 * key from `show_game_genie`, which older versions read for the bottom bar button and the search
 * redirect, so that key can be switched off without hiding GameGenie in this version.
 */
const val GAME_GENIE_ENTRY_POINTS_FLAG = "game_genie_entry_points"

/** The text carried from the search bar into the chat: a valid search, trimmed, or nothing. */
fun askGameGenieQuery(searchValue: String): String? =
  searchValue.trim().takeIf { it.isValidSearch() }

fun buildAskGameGenieRoute(
  entryPoint: GameGenieEntryPoint,
  searchValue: String = "",
): String = buildGameGenieRoute(query = askGameGenieQuery(searchValue), entryPoint = entryPoint)
  .withBundleMeta(BundleMeta("gamegenie-${entryPoint.source}", "app"))

/**
 * The action behind every GameGenie entry point: report where it was opened from, then open the
 * chat carrying the search text, if any. Null when the entry points are off, so callers hide them.
 */
@Composable
fun rememberAskGameGenie(
  navigate: (String) -> Unit,
): ((entryPoint: GameGenieEntryPoint, searchValue: String) -> Unit)? {
  val enabled = rememberGameGenieEntryPointsEnabled()
  val analytics = rememberGameGenieAnalytics()
  return if (enabled) {
    { entryPoint, searchValue ->
      analytics.sendGameGenieEntryClick(entryPoint)
      navigate(buildAskGameGenieRoute(entryPoint, searchValue))
    }
  } else {
    null
  }
}
