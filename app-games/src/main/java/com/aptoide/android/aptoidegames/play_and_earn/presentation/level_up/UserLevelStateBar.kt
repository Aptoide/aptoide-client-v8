package com.aptoide.android.aptoidegames.play_and_earn.presentation.level_up

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.TierBadge
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.hexagons.getHexagonLevelCurrent
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.hexagons.getHexagonLevelLocked
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.hexagons.getHexagonLevelUnlocked
import com.aptoide.android.aptoidegames.play_and_earn.domain.Level
import com.aptoide.android.aptoidegames.play_and_earn.domain.UserStats
import com.aptoide.android.aptoidegames.play_and_earn.domain.levels
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun UserLevelStateBar(levels: List<Level>, userStats: UserStats) {
  val progress =
    userStats.currentAmount / (userStats.nextLevelAmount ?: userStats.currentAmount).toFloat()

  Box {
    Column(
      modifier = Modifier
        .padding(top = 32.dp)
        .padding(start = 63.dp),
      verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
      levels.forEachIndexed { index, level ->
        when {
          level.level < userStats.level -> {
            Box(
              modifier = Modifier
                .width(3.dp)
                .height(40.dp)
                .fillMaxHeight()
                .background(Palette.Yellow100)
            )
          }

          level.level == userStats.level -> {
            Box(
              modifier = Modifier
                .width(3.dp)
                .height(40.dp)
                .fillMaxHeight()
                .background(Palette.Grey)
            ) {
              Box(
                modifier = Modifier
                  .width(3.dp)
                  .fillMaxHeight(progress)
                  .background(Palette.Yellow100)
              ) {
                Box(
                  modifier = Modifier
                    .wrapContentSize(unbounded = true)
                    .size(10.dp)
                    .offset(y = 5.dp)
                    .border(3.dp, Palette.Yellow100, CircleShape)
                    .background(Palette.Black)
                    .align(Alignment.BottomCenter)
                )
              }
            }
          }

          else -> {
            if (index < levels.size - 1) {
              Box(
                modifier = Modifier
                  .width(3.dp)
                  .height(40.dp)
                  .fillMaxHeight()
                  .background(Palette.Grey)
              )
            }
          }
        }
      }
    }

    Column(
      verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
      levels.forEach {
        UserLevelStateBarItem(level = it, userStats = userStats)
      }
    }
  }
}

@Composable
private fun UserLevelStateBarItem(level: Level, userStats: UserStats) {
  val levelProperties = LevelProperties.fromLevel(level.level)

  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    Box(
      modifier = Modifier.size(50.dp, 32.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = level.amount.toString(),
        style = AGTypography.InputsM,
        color = Palette.Yellow100,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1
      )
    }

    Image(
      imageVector = when {
        level.level < userStats.level -> getHexagonLevelUnlocked()
        level.level == userStats.level -> getHexagonLevelCurrent()
        else -> getHexagonLevelLocked()
      },
      contentDescription = null
    )

    Row(
      modifier = Modifier.padding(all = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      Box(
        modifier = Modifier
          .height(26.dp)
          .background(levelProperties.mainColor.copy(alpha = 0.2f))
          .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "Lvl ${level.level + 1}",
          style = AGTypography.InputsS,
          color = levelProperties.mainColor
        )
      }

      TierBadge(levelProperties = LevelProperties.fromLevel(level.level))

      Box(
        modifier = Modifier
          .height(26.dp)
          .background(Palette.Secondary)
          .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "${level.bonus}% Bonus",
          style = AGTypography.InputsS,
          color = Palette.White
        )
      }
    }
  }
}

@Preview
@Composable
fun UserLevelStateBarPreview() {
  UserLevelStateBar(
    levels = levels,
    userStats = UserStats(
      currentAmount = 35000f,
      currentAmountCurrency = 35000f,
      level = 2,
      nextLevelAmount = 50000,
      nextLevelAmountCurrency = 50000
    )
  )
}