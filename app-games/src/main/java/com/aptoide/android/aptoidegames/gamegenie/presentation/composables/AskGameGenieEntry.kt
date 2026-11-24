package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

/** The low-key way into GameGenie from search, replacing the bottom bar button (APT-247). */
@Composable
fun AskGameGenieEntry(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier
      .clickable(role = Role.Button, onClick = onClick)
      .minimumInteractiveComponentSize()
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Image(
      modifier = Modifier.size(24.dp),
      imageVector = getGameGenieAiToolbarIcon(Palette.Primary),
      contentDescription = null,
    )
    Text(
      modifier = Modifier
        .padding(start = 16.dp)
        .weight(1f),
      text = stringResource(R.string.gamegenie_search_entry_title),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      style = AGTypography.DescriptionGames,
      color = Palette.Primary,
    )
  }
}

@PreviewDark
@Composable
private fun AskGameGenieEntryPreview() {
  AptoideTheme {
    AskGameGenieEntry(onClick = {})
  }
}
