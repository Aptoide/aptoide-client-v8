package com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import cm.aptoide.pt.extensions.getPackageInfo
import cm.aptoide.pt.extensions.runPreviewable

private const val ROBLOX_PACKAGE = "com.roblox.client"
private val FREE_FIRE_PACKAGES = listOf("com.dts.freefireth", "com.dts.freefiremax")

/**
 * The preferred [PaERewardType] to surface for the current user, derived from which
 * supported games are installed on the device:
 *  - Roblox installed (with or without Free Fire)
 *  - Only Free Fire / Free Fire Max installed
 *  - Neither installed
 */
@Composable
fun rememberPreferredPaEReward(): PaERewardType = runPreviewable(
  preview = { PaERewardType.entries.random() },
  real = {
    val context = LocalContext.current
    remember(context) {
      val pm = context.packageManager
      val robloxInstalled = pm.getPackageInfo(ROBLOX_PACKAGE) != null
      val freeFireInstalled = FREE_FIRE_PACKAGES.any { pm.getPackageInfo(it) != null }
      when {
        robloxInstalled -> PaERewardType.ROBUX
        freeFireInstalled -> PaERewardType.DIAMONDS
        else -> PaERewardType.ROBUX
      }
    }
  }
)
