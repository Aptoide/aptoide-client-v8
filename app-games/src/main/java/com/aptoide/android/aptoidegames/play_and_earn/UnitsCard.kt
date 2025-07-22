package com.aptoide.android.aptoidegames.play_and_earn

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
  availableUnits: Int,
  modifier: Modifier = Modifier
) {
  val annotatedString = buildAnnotatedString {
    withStyle(style = AGTypography.InputsL.toSpanStyle().copy(color = Palette.Yellow)) {
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
        text = "Aptoide Balance", //TODO: hardcoded string
        style = AGTypography.InputsM,
        color = Palette.White,
      )

      ConstraintLayoutUnitsBar(
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

@Composable
private fun SpendableUnitsProgressBar(
  availableUnits: Int,
  modifier: Modifier = Modifier
) {
  /*
  var targetProgress by remember { mutableFloatStateOf(0f) }

  LaunchedEffect(Unit) {
    targetProgress = progressFromUnits(availableUnits)
  }

  //LinearOutSlowInEasing

  val animatedProgress by animateFloatAsState(
    targetValue = targetProgress.coerceIn(0f, 1f),
    animationSpec = tween(durationMillis = 2000, easing = EaseOut),
    label = "progress"
  )
   */


  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    Text(
      modifier = Modifier.align(Alignment.Bottom).alignByBaseline(),
      text = "0",
      style = AGTypography.InputsS,
      color = Palette.White
    )

    CustomLayoutTest(
      modifier = Modifier.weight(1f).alignByBaseline()
    )

    Text(
      modifier = Modifier.align(Alignment.Bottom).alignByBaseline(),
      text = "100",
      style = AGTypography.InputsS,
      color = Palette.White
    )
  }
}

private fun progressFromUnits(units: Int): Float = (units % 100).let {
  if (it == 0 && units != 0) 1f else (it / 100f)
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
