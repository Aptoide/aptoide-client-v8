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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.tier.getPlusTierIcon
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.tier.getTierCoinIcon
import com.aptoide.android.aptoidegames.play_and_earn.domain.Tier
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun TierBadge(tier: Tier) {
  val colors = tier.getDefaultColors()

  Row(
    modifier = Modifier
      .height(26.dp)
      .border(2.dp, colors.center)
      .background(colors.center.copy(alpha = 0.2f))
      .padding(horizontal = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    Image(
      imageVector = getTierCoinIcon(tier.getDefaultColors()),
      contentDescription = null
    )
    Text(
      text = tier.getTierName(),
      style = AGTypography.InputsS,
      color = tier.getTierBadgeTextColor()
    )
    if (tier.name.contains("PLUS")) {
      Image(
        imageVector = getPlusTierIcon(tier.getPlusTierIconColor()),
        contentDescription = null
      )
    }
  }
}

enum class TierColors(val left: Color, val center: Color, val right: Color) {
  ZERO(Color.Transparent, Color.Transparent, Color.Transparent),
  BRONZE(Palette.Yellow100, Palette.Orange200, Palette.Orange150),
  SILVER(Palette.White, Palette.Blue100, Palette.Blue200),
  GOLD(Palette.Yellow50, Palette.Yellow200, Palette.Yellow100),
  PLATINUM(Palette.Blue50, Palette.Blue150, Palette.Blue250),
  VIP(Palette.Yellow100, Palette.Yellow150, Palette.Yellow200)
}

fun Tier.getDefaultColors() = when (this) {
  Tier.ZERO -> TierColors.ZERO
  Tier.BRONZE -> TierColors.BRONZE
  Tier.BRONZE_PLUS -> TierColors.BRONZE
  Tier.SILVER -> TierColors.SILVER
  Tier.SILVER_PLUS -> TierColors.SILVER
  Tier.GOLD -> TierColors.GOLD
  Tier.GOLD_PLUS -> TierColors.GOLD
  Tier.PLATINUM -> TierColors.PLATINUM
  Tier.PLATINUM_PLUS -> TierColors.PLATINUM
  Tier.VIP -> TierColors.VIP
  Tier.VIP_PLUS -> TierColors.VIP
}

private fun Tier.getTierName() = when (this) {
  Tier.ZERO -> ""

  Tier.BRONZE,
  Tier.BRONZE_PLUS -> "Bronze"

  Tier.SILVER,
  Tier.SILVER_PLUS -> "Silver"

  Tier.GOLD,
  Tier.GOLD_PLUS -> "Gold"

  Tier.PLATINUM,
  Tier.PLATINUM_PLUS -> "Platinum"

  Tier.VIP,
  Tier.VIP_PLUS -> "VIP"
}

fun Tier.getTierBadgeTextColor() = when (this) {
  Tier.ZERO -> this.getDefaultColors().center

  Tier.BRONZE,
  Tier.BRONZE_PLUS -> this.getDefaultColors().right

  Tier.SILVER,
  Tier.SILVER_PLUS -> this.getDefaultColors().center

  Tier.GOLD,
  Tier.GOLD_PLUS -> this.getDefaultColors().right

  Tier.PLATINUM,
  Tier.PLATINUM_PLUS -> this.getDefaultColors().center

  Tier.VIP,
  Tier.VIP_PLUS -> this.getDefaultColors().left
}

fun Tier.getPlusTierIconColor() = getTierBadgeTextColor()

@Preview
@Composable
private fun TierBadgePreview() {
  TierBadge(tier = Tier.VIP)
}
