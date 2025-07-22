package com.aptoide.android.aptoidegames.play_and_earn

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getGiftPinBackground
import com.aptoide.android.aptoidegames.play_and_earn.animations.PlayAndEarnAnimatedGift
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun CustomLayoutTest(
  modifier: Modifier = Modifier
) {
  Layout(
    modifier = modifier.fillMaxWidth(),
    content = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "0",
          style = AGTypography.InputsS,
          color = Palette.White
        )
        Box(
          modifier = Modifier
            .weight(1f)
            .height(8.dp)
            .background(Palette.Grey)
        )
        Text(
          text = "100",
          style = AGTypography.InputsS,
          color = Palette.White
        )
      }


      Box(
        modifier = Modifier
          .fillMaxWidth(0.6f)
          .height(8.dp)
          .background(Palette.Yellow)
      )

      Box(
        modifier = Modifier
          .width(1.dp)
          .height(10.dp)
          .background(Palette.White)
      )

      Box(
        contentAlignment = Alignment.TopCenter
      ) {
        Image(
          imageVector = getGiftPinBackground(),
          contentDescription = null
        )
        PlayAndEarnAnimatedGift()
      }
    },
    measurePolicy = { measurables, constraints ->
      val gray = measurables[0].measure(constraints.copy(minWidth = 0, minHeight = 0))
      val yellow = measurables[1].measure(constraints.copy(minWidth = 0, minHeight = 0))
      val white = measurables[2].measure(constraints.copy(minWidth = 0, minHeight = 0))
      val blue = measurables[3].measure(constraints.copy(minWidth = 0, minHeight = 0))

      layout(constraints.maxWidth, blue.height + white.height) {
        listOf(gray, yellow, white, blue).forEachIndexed { index, placeable ->
          when (index) {
            0 -> placeable.place(0, blue.height + ((white.height - gray.height) / 2))
            1 -> placeable.place(0, blue.height + ((white.height - yellow.height) / 2))
            2 -> placeable.place(yellow.width - white.width, blue.height)
            3 -> placeable.place(yellow.width - (blue.width / 2) - white.width, 0)
          }
        }
      }
    }
  )
}

@Preview
@Composable
fun CustomLayoutTestPreview() {
  CustomLayoutTest()
}