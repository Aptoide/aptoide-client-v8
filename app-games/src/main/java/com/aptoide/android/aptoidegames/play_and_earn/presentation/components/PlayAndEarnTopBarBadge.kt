package com.aptoide.android.aptoidegames.play_and_earn.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.drawables.backgrounds.getBadgeGiftBackground
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.TierBadge
import com.aptoide.android.aptoidegames.play_and_earn.presentation.analytics.rememberPaEAnalytics
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.animations.PaEAnimatedGift
import com.aptoide.android.aptoidegames.play_and_earn.presentation.level_up.LevelProperties
import com.aptoide.android.aptoidegames.play_and_earn.presentation.level_up.rememberCurrentPaELevel
import com.aptoide.android.aptoidegames.play_and_earn.presentation.level_up.rememberWalletUnits
import com.aptoide.android.aptoidegames.play_and_earn.rememberShouldShowPlayAndEarn

@Composable
fun PlayAndEarnTopBarBadge(onClick: () -> Unit) {
  val currentLevel = rememberCurrentPaELevel()
  val shouldShowPlayAndEarn = rememberShouldShowPlayAndEarn()
  val paeAnalytics = rememberPaEAnalytics()
  val walletUnits = rememberWalletUnits()

  val hasUnitsToExchange = walletUnits != null && walletUnits >= 100L

  if (shouldShowPlayAndEarn && currentLevel != null) {
    Row(
      modifier = Modifier.clickable(
        onClick = {
          paeAnalytics.sendPaEActionBarBadgeClick()
          onClick()
        }
      ),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      TierBadge(levelProperties = LevelProperties.Companion.fromLevel(currentLevel))

      if (hasUnitsToExchange) {
        Box(
          modifier = Modifier.offset(x = (-6).dp, y = (-4).dp),
          contentAlignment = Alignment.Center
        ) {
          Image(
            imageVector = getBadgeGiftBackground(),
            contentDescription = null
          )
          PaEAnimatedGift(
            modifier = Modifier.size(24.dp, 30.dp)
          )
        }
      }
    }
  }
}

@Preview
@Composable
fun PlayAndEarnTopBarBadgePreview() {
  PlayAndEarnTopBarBadge(onClick = {})
}
