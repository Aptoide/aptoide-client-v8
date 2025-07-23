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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.TierBadge
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getDefaultColors
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getTierBadgeTextColor
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.hexagons.getHexagonLevelCurrent
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.hexagons.getHexagonLevelLocked
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.hexagons.getHexagonLevelUnlocked
import com.aptoide.android.aptoidegames.play_and_earn.domain.Level
import com.aptoide.android.aptoidegames.play_and_earn.domain.LevelCheckpointStatus
import com.aptoide.android.aptoidegames.play_and_earn.domain.Tier
import com.aptoide.android.aptoidegames.play_and_earn.domain.levels
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun UserLevelStateBar(levels: List<Level>, currentLevel: Level) {
  val currentXp = currentLevel.xp
  val currentLevelMinXp = levels.find { it.level == currentLevel.level }?.xp ?: currentLevel.xp
  val nextLevelXp = levels.find { it.level == currentLevel.level + 1 }?.xp ?: currentLevel.xp

  val progress = (currentXp - currentLevelMinXp) / (nextLevelXp - currentLevelMinXp).toFloat()

  Box {
    Column(
      modifier = Modifier
        .padding(top = 32.dp)
        .padding(start = 63.dp),
      verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
      levels.forEachIndexed { index, checkpoint ->
        when (checkpoint.status) {
          LevelCheckpointStatus.UNLOCKED -> {
            Box(
              modifier = Modifier
                .width(3.dp)
                .height(40.dp)
                .fillMaxHeight()
                .background(Palette.Yellow)
            )
          }

          LevelCheckpointStatus.CURRENT -> {
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
                  .background(Palette.Yellow)
              ) {
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

          LevelCheckpointStatus.LOCKED -> {
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
        UserLevelStateBarItem(it)
      }
    }
  }
}

@Composable
private fun UserLevelStateBarItem(checkpoint: Level) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    Box(
      modifier = Modifier.size(50.dp, 32.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = checkpoint.xp.toString(),
        style = AGTypography.InputsM,
        color = Palette.Yellow
      )
    }

    Image(
      imageVector = when (checkpoint.status) {
        LevelCheckpointStatus.UNLOCKED -> getHexagonLevelUnlocked()
        LevelCheckpointStatus.CURRENT -> getHexagonLevelCurrent()
        LevelCheckpointStatus.LOCKED -> getHexagonLevelLocked()
      },
      contentDescription = null
    )

    if (checkpoint.tier == Tier.ZERO) {
      Box(
        modifier = Modifier.height(42.dp)
      )
    } else {
      Row(
        modifier = Modifier.padding(all = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Box(
          modifier = Modifier
            .height(26.dp)
            .background(checkpoint.tier.getDefaultColors().center.copy(alpha = 0.2f))
            .padding(horizontal = 8.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "Lvl ${checkpoint.level}",
            style = AGTypography.InputsS,
            color = checkpoint.tier.getTierBadgeTextColor()
          )
        }

        TierBadge(tier = checkpoint.tier)

        Box(
          modifier = Modifier
            .height(26.dp)
            .background(Palette.Secondary)
            .padding(horizontal = 8.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "${checkpoint.bonus}% Bonus",
            style = AGTypography.InputsS,
            color = Palette.White
          )
        }
      }
    }
  }
}

@Preview
@Composable
fun UserLevelStateBarPreview() {
  UserLevelStateBar(
    levels = levels,
    currentLevel = Level(
      xp = 1500,
      level = 2,
      tier = Tier.BRONZE_PLUS,
      bonus = 0f,
      status = LevelCheckpointStatus.CURRENT
    )
  )
}