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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.wallet.gamification.domain.GamificationLevelStatus
import cm.aptoide.pt.wallet.gamification.domain.GamificationStats
import cm.aptoide.pt.wallet.gamification.domain.Level
import cm.aptoide.pt.wallet.gamification.domain.Levels
import cm.aptoide.pt.wallet.gamification.domain.previewLevels
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.TierBadge
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.hexagons.getHexagonLevelCurrent
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.hexagons.getHexagonLevelLocked
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.hexagons.getHexagonLevelUnlocked
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import java.math.BigDecimal
import java.text.DecimalFormat
import kotlin.math.ln
import kotlin.math.pow

@Composable
fun UserLevelStateBar(levels: Levels, gamificationStats: GamificationStats) {
  val progress =
    gamificationStats.totalSpend.divide(
      gamificationStats.nextLevelAmount ?: gamificationStats.totalSpend
    ).toFloat()

  val levelList = levels.levelList

  //Vertical level bar
  Box {
    Column(
      modifier = Modifier
        .padding(top = 32.dp)
        .padding(start = 63.dp),
      verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
      levelList.forEachIndexed { index, level ->
        when {
          level.level < gamificationStats.level -> { //Completed
            Box(
              modifier = Modifier
                .width(3.dp)
                .height(40.dp)
                .fillMaxHeight()
                .background(Palette.Yellow100)
            )
          }

          level.level == gamificationStats.level -> { //Current
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
            if (index < levelList.size - 1) { //Not started
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

    //Horizontal level items
    Column(
      verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
      levelList.forEach {
        UserLevelStateBarItem(level = it, gamificationStats = gamificationStats)
      }
    }
  }
}

@Composable
private fun UserLevelStateBarItem(level: Level, gamificationStats: GamificationStats) {
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
        text = level.amount.shorten(),
        style = AGTypography.InputsM,
        color = Palette.Yellow100,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1
      )
    }

    Image(
      imageVector = when {
        level.level < gamificationStats.level -> getHexagonLevelUnlocked()
        level.level == gamificationStats.level -> getHexagonLevelCurrent()
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
          modifier = Modifier.wrapContentWidth(unbounded = true),
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
          modifier = Modifier.wrapContentWidth(unbounded = true),
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
    levels = previewLevels,
    gamificationStats = GamificationStats(
      totalSpend = BigDecimal.valueOf(35000),
      level = 2,
      nextLevelAmount = BigDecimal.valueOf(50000),
      gamificationStatus = GamificationLevelStatus.STANDARD
    )
  )
}

private fun BigDecimal.shorten(): String {
  if (this < BigDecimal(100_000)) return this.toRoundedString()

  val suffixes = arrayOf("", "K", "M", "B", "T")
  val exp = (ln(this.toDouble()) / ln(1000.0)).toInt()

  val df = DecimalFormat("#.#")
  val value = this.toDouble() / 1000.0.pow(exp.toDouble())

  return df.format(value) + suffixes[exp]
}