package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.gamegenie.analytics.GameGenieEntryPoint
import com.aptoide.android.aptoidegames.gamegenie.analytics.rememberGameGenieAnalytics
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

/**
 * The last tile of the My Games row: a way into GameGenie for help with games already installed.
 * Sized like a game tile so it does not stand out from the row (APT-247).
 */
@Composable
fun GameGenieMyGamesTile(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val analytics = rememberGameGenieAnalytics()
  // The row is lazy, so this runs when the tile is scrolled into view.
  LaunchedEffect(Unit) {
    analytics.sendGameGenieEntryImpression(GameGenieEntryPoint.MY_GAMES)
  }

  Column(
    modifier = modifier
      .semantics(mergeDescendants = true) { }
      .clickable(role = Role.Button, onClick = onClick)
      .width(88.dp)
  ) {
    Box(
      modifier = Modifier
        .padding(bottom = 8.dp)
        .size(88.dp)
        .border(width = 1.dp, color = Palette.Primary),
      contentAlignment = Alignment.Center,
    ) {
      Image(
        modifier = Modifier.size(40.dp),
        imageVector = getGameGenieAiToolbarIcon(Palette.Primary),
        contentDescription = null,
      )
    }
    Text(
      text = stringResource(R.string.gamegenie_my_games_entry_title),
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier.defaultMinSize(minHeight = 36.dp),
      style = AGTypography.DescriptionGames,
      color = Palette.Primary,
    )
  }
}

@PreviewDark
@Composable
private fun GameGenieMyGamesTilePreview() {
  AptoideTheme {
    GameGenieMyGamesTile(onClick = {})
  }
}
