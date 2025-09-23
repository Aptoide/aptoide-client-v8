package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
  navigateBack: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp)
  ) {
    Image(
      imageVector = getLeftArrow(Palette.Primary, Palette.Black),
      contentDescription = stringResource(id = R.string.button_back_title),
      contentScale = ContentScale.Crop,
      modifier = Modifier
        .padding(top = 8.dp)
        .clickable(onClick = navigateBack)
        .size(32.dp)
    )

    Box(
      modifier = Modifier
        .weight(1f),
      contentAlignment = Alignment.Center
    ) {
      GameCompanionIcon(
        game = game,
        imageSize = 80,
        textSize = 20.sp,
        clickableEnabled = false,
        modifier = Modifier,
      )
    }

    Box(modifier = Modifier.padding(end = 16.dp).size(32.dp))
  }
}
