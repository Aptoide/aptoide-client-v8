package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun ChatParticipantName(
  text: String,
  modifier: Modifier = Modifier,
) {
  Text(
    text = text,
    style = AGTypography.InputsXS,
    color = Palette.Primary,
    modifier = modifier
      .padding(bottom = 4.dp)
  )
}
