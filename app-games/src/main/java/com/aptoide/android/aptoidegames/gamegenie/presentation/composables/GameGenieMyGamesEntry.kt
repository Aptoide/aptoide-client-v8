package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.gamegenie.analytics.GameGenieEntryPoint
import com.aptoide.android.aptoidegames.gamegenie.analytics.rememberGameGenieAnalytics

/**
 * The line under the My Games row: a way into GameGenie for help with games already installed
 * (APT-247).
 */
@Composable
fun GameGenieMyGamesEntry(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val analytics = rememberGameGenieAnalytics()
  // Home is a lazy list, so this is composed when My Games comes into view. Saved state keeps it
  // to one impression per home screen, not one each time the bundle is scrolled back or returned to.
  var impressionSent by rememberSaveable { mutableStateOf(false) }
  LaunchedEffect(Unit) {
    if (!impressionSent) {
      impressionSent = true
      analytics.sendGameGenieEntryImpression(GameGenieEntryPoint.MY_GAMES)
    }
  }

  AskGameGenieEntry(
    onClick = onClick,
    modifier = modifier,
    text = stringResource(R.string.gamegenie_my_games_entry_title),
  )
}
