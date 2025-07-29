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
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.animations.PaEAnimatedGift
import com.aptoide.android.aptoidegames.play_and_earn.presentation.level_up.LevelProperties
import com.aptoide.android.aptoidegames.play_and_earn.presentation.level_up.rememberCurrentPaELevel

@Composable
fun PlayAndEarnTopBarBadge(onClick: () -> Unit) {
  val currentLevel = rememberCurrentPaELevel()

  currentLevel?.let {
    Row(
      modifier = Modifier.clickable(onClick = onClick),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      TierBadge(levelProperties = LevelProperties.Companion.fromLevel(it))
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

@Preview
@Composable
fun PlayAndEarnTopBarBadgePreview() {
  PlayAndEarnTopBarBadge(onClick = {})
}