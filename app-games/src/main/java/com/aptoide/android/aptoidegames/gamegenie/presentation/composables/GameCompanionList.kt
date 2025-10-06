package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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

  FlowRow(
    modifier = Modifier
      .padding(start = 18.dp, end = 18.dp, top = 24.dp, bottom = 19.dp),
    horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    maxItemsInEachRow = 4
  ) {
    displayGames.forEach { game ->
      if (game != null) {
        GameCompanionIcon(
          game = game,
          onClick = onClick,
          imageSize = 64,
          showAnimation = false
        )
      } else {
        GameIconShimmerLoading(
          iconSize = 64,
          textWidth = 64,
          spaceBetween = 10,
        )
      }
    }
  }
}

@Composable
fun GameIconShimmerLoading(
  iconSize: Int = 56,
  textHeight: Int = 12,
  textWidth: Int = 56,
  spaceBetween: Int = 16,
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      modifier = Modifier
        .size(iconSize.dp)
        .shimmerLoading(Palette.Primary)
    )
    Spacer(modifier = Modifier.height(spaceBetween.dp))
    Box(
      modifier = Modifier
        .height(textHeight.dp)
        .width(textWidth.dp)
        .shimmerLoading(Palette.Primary)
    )
  }
}
