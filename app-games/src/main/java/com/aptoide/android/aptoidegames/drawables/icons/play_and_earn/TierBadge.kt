package com.aptoide.android.aptoidegames.drawables.icons.play_and_earn

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.tier.getPlusTierIcon
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.tier.getTierCoinIcon
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.tier.getVipTierCoinIcon
import com.aptoide.android.aptoidegames.play_and_earn.presentation.level_up.LevelProperties
import com.aptoide.android.aptoidegames.play_and_earn.presentation.level_up.isVIP
import com.aptoide.android.aptoidegames.theme.AGTypography
import kotlin.random.Random
import kotlin.random.nextInt

@Composable
fun TierBadge(levelProperties: LevelProperties) {
  Row(
    modifier = Modifier
      .height(26.dp)
      .border(2.dp, levelProperties.mainColor)
      .background(levelProperties.mainColor.copy(alpha = 0.2f))
      .padding(horizontal = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    Image(
      imageVector = if (levelProperties.isVIP()) {
        getVipTierCoinIcon()
      } else {
        getTierCoinIcon(levelProperties.level)
      },
      contentDescription = null
    )
    Text(
      text = levelProperties.name,
      style = AGTypography.InputsS,
      color = levelProperties.mainColor
    )
    if (levelProperties.isPlusVariant) {
      Image(
        imageVector = getPlusTierIcon(levelProperties.mainColor),
        contentDescription = null
      )
    }
  }
}

@Preview
@Composable
private fun TierBadgePreview() {
  TierBadge(levelProperties = LevelProperties.fromLevel(Random.nextInt(0..9)))
}
