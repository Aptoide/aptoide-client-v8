package com.aptoide.android.aptoidegames.play_and_earn

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.hexagons.getHexagonLevelUnlocked
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun UserLevelBarTestTwo(numberOfLevels: Int) {
  Box {
    Column(
      modifier = Modifier
        .padding(top = 32.dp)
        .padding(start = (63).dp),
      verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
      repeat(numberOfLevels - 1) {
        Box(
          modifier = Modifier
            .width(3.dp)
            .height(40.dp)
            .fillMaxHeight(if (it == 1) 1f else 1f)
            .background(Palette.Grey)
        ) {
          Box(
            modifier = Modifier
              .width(3.dp)
              .fillMaxHeight(if (it == 1) 0.5f else 1f)
              .background(Palette.Yellow)
          ) {
            if (it == 1) {
              Box(
                modifier = Modifier
                  .wrapContentSize(unbounded = true)
                  .size(10.dp)
                  .offset(y = 5.dp)
                  .border(3.dp, Palette.Yellow, CircleShape)
                  .background(Palette.Black)
                  .align(Alignment.BottomCenter)
              )
            }
          }
        }
      }
    }

    Column(
      verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
      repeat(numberOfLevels) {
        UserLevelBarTestTwoItem()
      }
    }
  }
}

@Composable
fun UserLevelBarTestTwoItem() {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    Box(
      modifier = Modifier.size(50.dp, 32.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "1000",
        style = AGTypography.InputsM,
        color = Palette.Yellow
      )
    }

    Image(
      imageVector = getHexagonLevelUnlocked(),
      contentDescription = null
    )

    Row(
      modifier = Modifier.padding(all = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      Box(
        modifier = Modifier
          .height(26.dp)
          .background(Color(0xFFF5893233).copy(alpha = 0.2f))
          .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "Lvl 1",
          style = AGTypography.InputsS,
          color = Color(0xFFF58932)
        )
      }
      Box(
        modifier = Modifier
          .height(26.dp)
          .background(Color(0xFFF5893233).copy(alpha = 0.2f))
          .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "Bronze",
          style = AGTypography.InputsS,
          color = Color(0xFFF58932)
        )
      }
      Box(
        modifier = Modifier
          .height(26.dp)
          .background(Palette.Secondary)
          .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "5% Bonus",
          style = AGTypography.InputsS,
          color = Palette.White
        )
      }
    }
  }
}

@Preview
@Composable
fun UserLevelBarTestTwoPreview() {
  UserLevelBarTestTwo(6)
}