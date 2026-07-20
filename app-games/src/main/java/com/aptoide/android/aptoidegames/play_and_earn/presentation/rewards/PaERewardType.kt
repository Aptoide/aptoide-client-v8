package com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import cm.aptoide.pt.play_and_earn.exchange.domain.RedeemType
import com.aptoide.android.aptoidegames.R

/** Display fallback for the reward amount while the FIRST_SIGN_IN mission hasn't resolved. */
const val PAE_DEFAULT_REWARD_AMOUNT = "$0.50"

private const val ROBLOX_PACKAGE = "com.roblox.client"
private val FREE_FIRE_PACKAGES = setOf("com.dts.freefireth", "com.dts.freefiremax")

enum class PaERewardType(@StringRes val displayNameRes: Int) {
  ROBUX(R.string.play_and_earn_currency_robux),
  DIAMONDS(R.string.play_and_earn_currency_diamonds),
  ;

  companion object {
    fun fromPackageName(packageName: String?): PaERewardType? = when (packageName) {
      ROBLOX_PACKAGE -> ROBUX
      in FREE_FIRE_PACKAGES -> DIAMONDS
      else -> null
    }
  }
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

@get:DrawableRes
val PaERewardType.featureGraphicRes: Int
  get() = when (this) {
    PaERewardType.ROBUX -> R.drawable.roblox_feature_graphic
    PaERewardType.DIAMONDS -> R.drawable.free_fire_feature_graphic
  }

class PaERewardTypeProvider : PreviewParameterProvider<PaERewardType> {
  override val values: Sequence<PaERewardType> = PaERewardType.entries.asSequence()
}
