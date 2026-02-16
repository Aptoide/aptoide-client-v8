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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.toAnnotatedString
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun UnitsCard(
  availableUnits: Long,
  modifier: Modifier = Modifier
) {
  val originalString =
    stringResource(id = R.string.play_and_earn_units_limit_body, availableUnits)
  val annotatedString = originalString.toAnnotatedString(
    AGTypography.InputsL.toSpanStyle().copy(color = Palette.Yellow100)
  )

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
        text = stringResource(R.string.play_and_earn_your_units_title),
        style = AGTypography.InputsM,
        color = Palette.White,
      )

      UnitsBar(
        modifier = Modifier.padding(horizontal = 16.dp),
        availableUnits = availableUnits
      )

      Text(
        text = annotatedString,
        style = AGTypography.InputsXSRegular,
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
