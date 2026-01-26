package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.drawables.icons.getLeftArrow
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun ChatBackButton(
  modifier: Modifier = Modifier,
  navigateBack: () -> Unit,
) {
  Row(
    modifier = modifier
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
  }
}
