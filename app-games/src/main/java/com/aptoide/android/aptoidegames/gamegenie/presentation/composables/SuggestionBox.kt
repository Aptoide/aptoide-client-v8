package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun SuggestionBox(
  suggestion: String,
  onClick: (String, Int) -> Unit,
  index: Int,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 8.dp),
    horizontalArrangement = Arrangement.End
  ) {
    Column(
      modifier = Modifier
        .background(
          color = Palette.Primary.copy(alpha = 0.1f),
        )
        .border(2.dp, color = Palette.Primary)
        .width(272.dp)
        .clickable { onClick(suggestion, index) }
        .padding(12.dp),
      horizontalAlignment = Alignment.Start
    ) {
      Text(
        text = suggestion,
        style = AGTypography.Body,
        color = Palette.Primary,
      )
    }
  }
}