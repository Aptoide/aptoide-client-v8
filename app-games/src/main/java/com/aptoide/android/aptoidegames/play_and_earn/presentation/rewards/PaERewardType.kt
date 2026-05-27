package com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import cm.aptoide.pt.play_and_earn.exchange.domain.RedeemType
import com.aptoide.android.aptoidegames.R

/** Default reward amount granted by the Play & Earn APKFY / earned-reward flows. */
const val PAE_DEFAULT_REWARD_AMOUNT = "$0.50"

enum class PaERewardType(@StringRes val displayNameRes: Int) {
  ROBUX(R.string.play_and_earn_currency_robux),
  DIAMONDS(R.string.play_and_earn_currency_diamonds),
}

fun PaERewardType.toRedeemType(): RedeemType = when (this) {
  PaERewardType.ROBUX -> RedeemType.ROBLOX_ROBUX
  PaERewardType.DIAMONDS -> RedeemType.FREE_FIRE_DIAMOND
}

@get:DrawableRes
val PaERewardType.iconRes: Int
  get() = when (this) {
    PaERewardType.ROBUX -> R.drawable.robux
    PaERewardType.DIAMONDS -> R.drawable.ff_diamond
  }

class PaERewardTypeProvider : PreviewParameterProvider<PaERewardType> {
  override val values: Sequence<PaERewardType> = PaERewardType.entries.asSequence()
}
