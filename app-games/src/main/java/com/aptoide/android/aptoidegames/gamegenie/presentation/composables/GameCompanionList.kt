package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.shimmerLoading
import com.aptoide.android.aptoidegames.gamegenie.domain.GameCompanion
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun GameCompanionList(
  games: List<GameCompanion>?,
  onClick: (GameCompanion) -> Unit,
) {
  val displayGames = games.takeIf { !it.isNullOrEmpty() } ?: List(4) { null }

  Column(
    modifier = Modifier
      .padding(start = 22.dp, end = 22.dp, top = 16.dp, bottom = 19.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    displayGames.chunked(4).forEach { rowGames ->
      Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        rowGames.forEach { game ->
          if (game != null) {
            GameCompanionIcon(
              game = game,
              onClick = onClick,
              imageSize = 56
            )
          }
        }
      }
    }
  }
}

