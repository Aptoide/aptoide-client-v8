package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun PoweredByAi() {
  return Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 32.dp),
    horizontalArrangement = Arrangement.Center
  ) {
    Text(
      text = stringResource(R.string.genai_powered_by_ai_title),
      style = AGTypography.Body,
      color = Palette.GreyLight,
    )
  }
}