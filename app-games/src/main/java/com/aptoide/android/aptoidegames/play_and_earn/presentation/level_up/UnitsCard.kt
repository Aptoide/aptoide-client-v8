package com.aptoide.android.aptoidegames.play_and_earn.presentation.level_up

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun UnitsCard(
  availableUnits: Long,
  modifier: Modifier = Modifier
) {
  val annotatedString = buildAnnotatedString {
    withStyle(style = AGTypography.InputsL.toSpanStyle().copy(color = Palette.Yellow100)) {
      append(availableUnits.toString())
    }
    withStyle(style = AGTypography.InputsXSRegular.toSpanStyle().copy(color = Palette.White)) {
      append("/100") //TODO: hardcoded string
    }
  }

  Box(
    modifier = modifier
      .requiredHeight(158.dp)
      .background(Palette.GreyDark),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Text(
        text = "Your Units", //TODO: hardcoded string
        style = AGTypography.InputsM,
        color = Palette.White,
      )

      UnitsBar(
        modifier = Modifier.padding(horizontal = 16.dp),
        availableUnits = availableUnits
      )

      Text(
        text = annotatedString,
        style = AGTypography.InputsL,
        color = Palette.White,
      )
    }
  }
}

@Preview
@Composable
fun SpendableUnitsProgressBarPreview() {
  Box(
    modifier = Modifier.size(156.dp, 158.dp)
  ) {
    UnitsCard(availableUnits = 175)
  }
}
