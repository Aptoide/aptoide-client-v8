package com.aptoide.android.aptoidegames.play_and_earn.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getSmallCoinIcon
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun PaEAppXPText(currentXp: Int, totalXp: Int) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Image(
      imageVector = getSmallCoinIcon(),
      contentDescription = null,
    )

    Text(
      text = getAppXPAnnotatedString(currentXp, totalXp),
      style = AGTypography.InputsS,
      color = Palette.White
    )
  }
}

fun getAppXPAnnotatedString(currentXp: Int, totalXp: Int): AnnotatedString {
  return buildAnnotatedString {
    withStyle(style = SpanStyle(color = Palette.Yellow100)) {
      append(currentXp.toString())
    }
    append("/$totalXp XP")
  }
}