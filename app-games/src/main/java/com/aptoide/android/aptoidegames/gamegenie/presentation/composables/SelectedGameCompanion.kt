package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.drawables.icons.getLeftArrow
import com.aptoide.android.aptoidegames.gamegenie.domain.GameCompanion
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun SelectedGameCompanion(
  game: GameCompanion,
  showImage: Boolean,
  navigateBack: () -> Unit,
  onLaunchOverlay: (packageName: String) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth(),
  ) {
    Image(
      imageVector = getLeftArrow(Palette.Primary, Palette.Black),
      contentDescription = stringResource(id = R.string.button_back_title),
      contentScale = ContentScale.Crop,
      modifier = Modifier
        .padding(vertical = 12.dp)
        .clickable(onClick = navigateBack)
        .size(32.dp)
    )

    Column(
      modifier = Modifier
        .weight(1f)
        .heightIn(56.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      if (showImage) {
        Spacer(modifier = Modifier.height(32.dp))
      }
      GameCompanionIcon(
        game = game,
        imageSize = 72,
        textSize = 20.sp,
        clickableEnabled = false,
        showImage = showImage,
        modifier = Modifier,
        animationSize = 136
      )
    }

    Box(
      modifier = Modifier
        .padding(vertical = 12.dp)
        .size(40.dp)
    ) {
      if (!showImage) {
        Image(
          imageVector = getLaunchGameGenieArrow(Palette.White, Palette.Secondary),
          contentDescription = stringResource(id = R.string.gamegenie_overlay_launch),
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .size(32.dp)
            .align(Alignment.Center)
            .clickable { onLaunchOverlay(game.packageName) }
        )

        Box(
          modifier = Modifier
            .matchParentSize()
            .align(Alignment.Center)
        ) {
          AnimationComposable(
            modifier = Modifier
              .width(40.dp)
              .height(40.dp),
            resId = R.raw.game_genie_launch_overlay_small,
          )
        }
      }
    }
  }
}
