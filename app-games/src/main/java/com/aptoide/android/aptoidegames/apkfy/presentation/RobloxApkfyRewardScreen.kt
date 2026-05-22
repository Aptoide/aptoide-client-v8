package com.aptoide.android.aptoidegames.apkfy.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.feature_apps.data.randomApp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.OverrideAnalyticsAPKFY
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.error_views.GenericErrorView
import com.aptoide.android.aptoidegames.mmp.WithUTM
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.SignInRewardViewModel
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.PAE_DEFAULT_REWARD_AMOUNT
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.PaERewardType
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.PendingPaEReward
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.RewardState
import com.aptoide.android.aptoidegames.play_and_earn.presentation.sign_in.rememberUserInfo
import com.aptoide.android.aptoidegames.theme.AptoideTheme

const val robloxApkfyRewardRoute = "robloxApkfyRewardRoute"

fun robloxApkfyRewardScreen(
  navigateToSignIn: () -> Unit,
  navigateToHome: () -> Unit,
) = ScreenData.withAnalytics(
  route = robloxApkfyRewardRoute,
  screenAnalyticsName = "RobloxApkfyReward",
) { _, navigate, navigateBack ->
  val apkfyState = rememberApkfyState()
  val apkfyAnalytics = rememberApkfyAnalytics()
  val viewModel = hiltViewModel<SignInRewardViewModel>()
  val userInfo = rememberUserInfo()
  val rewardState by viewModel.rewardState.collectAsState()
  var awaitingSignIn by rememberSaveable { mutableStateOf(false) }

  // The reward is only claimed once the user returns from the sign-in flow actually signed in.
  // After claiming we go back to home, so the success dialog shows there.
  LaunchedEffect(userInfo) {
    if (awaitingSignIn && userInfo != null && rewardState is RewardState.Unclaimed) {
      awaitingSignIn = false
      navigateToHome()
      viewModel.claim(
        PendingPaEReward(
          paERewardType = PaERewardType.ROBUX,
          rewardAmount = PAE_DEFAULT_REWARD_AMOUNT,
        )
      )
    }
  }

  BackHandler {
    apkfyAnalytics.sendApkfyScreenBackClicked()
    navigateBack()
  }

  TransparentStatusBarEffect()

  apkfyState?.data?.let { apkfyData ->
    WithUTM(
      source = apkfyData.utmSource,
      medium = apkfyData.utmMedium,
      campaign = apkfyData.utmCampaign,
      content = apkfyData.utmContent,
      term = apkfyData.utmTerm,
      navigate = navigate,
    ) { navigateWithUtm ->
      OverrideAnalyticsAPKFY(navigateWithUtm) { navigateTo ->
        ApkfyRewardScreenContent(
          app = apkfyData.app,
          headerImage = R.drawable.roblox_feature_graphic,
          rewardIcon = R.drawable.pae_roblox_reward_icon,
          paERewardType = PaERewardType.ROBUX,
          rewardAmount = PAE_DEFAULT_REWARD_AMOUNT,
          navigateBack = {
            apkfyAnalytics.sendApkfyScreenBackClicked()
            navigateBack()
          },
          onAppClick = { navigateTo(buildAppViewRoute(apkfyData.app)) },
          onCollect = {
            if (rewardState is RewardState.Unclaimed) {
              awaitingSignIn = true
              navigateToSignIn()
            }
          },
          onDismiss = navigateBack,
        )
      }
    }
  } ?: GenericErrorView(navigateBack)
}

@PreviewDark
@Composable
private fun RobloxApkfyRewardScreenPreview() {
  AptoideTheme {
    ApkfyRewardScreenContent(
      app = randomApp,
      headerImage = R.drawable.roblox_feature_graphic,
      rewardIcon = R.drawable.pae_roblox_reward_icon,
      paERewardType = PaERewardType.ROBUX,
      rewardAmount = PAE_DEFAULT_REWARD_AMOUNT,
      navigateBack = {},
      onAppClick = {},
      onCollect = {},
      onDismiss = {},
    )
  }
}
