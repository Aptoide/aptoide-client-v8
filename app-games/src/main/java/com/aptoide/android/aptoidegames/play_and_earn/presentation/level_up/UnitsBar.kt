package com.aptoide.android.aptoidegames.play_and_earn.presentation.level_up

import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getGiftPinBackground
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.animations.PaEAnimatedGift
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun UnitsBar(
  availableUnits: Long,
  modifier: Modifier = Modifier
) {
  var targetProgress by remember { mutableFloatStateOf(0f) }

  val animatedProgress by animateFloatAsState(
    targetValue = targetProgress.coerceIn(0f, 1f),
    animationSpec = tween(durationMillis = 2000, easing = EaseOut),
    label = "units"
  )

  LaunchedEffect(Unit) {
    targetProgress = progressFromUnits(availableUnits)
  }

  val backgroundColor: Color = when (availableUnits) {
    in 0..100 -> Palette.Grey
    in 101..200 -> Palette.Yellow100
    in 200..Int.MAX_VALUE -> Palette.Orange150
    else -> Palette.Grey
  }

  val progressColor: Color = when (availableUnits) {
    in 0..100 -> Palette.Yellow100
    in 101..200 -> Palette.Orange150
    in 200..Int.MAX_VALUE -> Palette.Orange200
    else -> Palette.Grey
  }

  ConstraintLayout(
    modifier = modifier
      .wrapContentHeight()
      .fillMaxWidth()
  ) {
    val (minUnitsText, leftSpacer, greyBar, progressBar, rightSpacer, maxUnitsText, pointer, pin) = createRefs()

    createHorizontalChain(
      minUnitsText,
      leftSpacer,
      progressBar,
      greyBar,
      rightSpacer,
      maxUnitsText,
      chainStyle = ChainStyle.Packed
    )

    Text(
      modifier = Modifier.constrainAs(minUnitsText) {
        start.linkTo(parent.start)
        bottom.linkTo(parent.bottom)
      },
      text = "0",
      style = AGTypography.InputsS,
      color = Palette.White
    )

    Spacer(
      modifier = Modifier
        .width(4.dp)
        .constrainAs(leftSpacer) {})

    Box(
      modifier = Modifier
        .height(8.dp)
        .background(backgroundColor)
        .constrainAs(greyBar) {
          this.centerVerticallyTo(minUnitsText)
          width = Dimension.fillToConstraints
          horizontalChainWeight = 1 - animatedProgress
        }
    )

    Spacer(
      modifier = Modifier
        .width(4.dp)
        .constrainAs(rightSpacer) {})

    Box(
      modifier = Modifier
        .height(8.dp)
        .background(progressColor)
        .constrainAs(progressBar) {
          this.centerVerticallyTo(minUnitsText)
          width = Dimension.fillToConstraints
          horizontalChainWeight = animatedProgress
        }
    )

    Box(
      modifier = Modifier
        .width(1.dp)
        .height(10.dp)
        .background(Palette.White)
        .constrainAs(pointer) {
          top.linkTo(progressBar.top)
          bottom.linkTo(progressBar.bottom)
          end.linkTo(progressBar.end)
        }
    )

    Box(
      modifier = Modifier.constrainAs(pin) {
        top.linkTo(parent.top)
        bottom.linkTo(pointer.top)
        this.centerHorizontallyTo(pointer)
      },
      contentAlignment = Alignment.TopCenter
    ) {
      Image(
        imageVector = getGiftPinBackground(),
        contentDescription = null
      )
      PaEAnimatedGift(
        modifier = Modifier.size(46.dp, 33.dp)
      )
    }

    Text(
      modifier = Modifier.constrainAs(maxUnitsText) {
        end.linkTo(parent.end)
        bottom.linkTo(parent.bottom)
      },
      text = "100",
      style = AGTypography.InputsS,
      color = Palette.White
    )
  }
}

private fun progressFromUnits(units: Long): Float = (units % 100L).let {
  if (it == 0L && units != 0L) 1f else (it / 100f)
}

@Preview
@Composable
fun ConstraintLayoutUnitsBarPreview() {
  UnitsBar(availableUnits = 90)
}
