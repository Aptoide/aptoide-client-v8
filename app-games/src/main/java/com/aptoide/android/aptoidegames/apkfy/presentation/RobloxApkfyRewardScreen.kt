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
import com.aptoide.android.aptoidegames.play_and_earn.presentation.analytics.PaEAnalytics
import com.aptoide.android.aptoidegames.play_and_earn.presentation.analytics.rememberPaEAnalytics
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.PAE_DEFAULT_REWARD_AMOUNT
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.PaERewardType
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.RewardState
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.SignInRewardViewModel
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
  val paeAnalytics = rememberPaEAnalytics()
  val viewModel = hiltViewModel<SignInRewardViewModel>()
  val userInfo = rememberUserInfo()
  val rewardState by viewModel.rewardState.collectAsState()
  // The reward amount comes from the backend FIRST_SIGN_IN mission (via SignInRewardRepository);
  // this screen only supplies the package-specific reward type.
  val rewardAmount = (rewardState as? RewardState.Unclaimed)?.reward?.rewardAmount
    ?: PAE_DEFAULT_REWARD_AMOUNT
  var awaitingSignIn by rememberSaveable { mutableStateOf(false) }

  // The reward is only claimed once the user returns from the sign-in flow actually signed in.
  // After claiming we go back to home, so the success dialog shows there.
  LaunchedEffect(userInfo) {
    val unclaimed = rewardState as? RewardState.Unclaimed
    if (awaitingSignIn && userInfo != null && unclaimed != null) {
      awaitingSignIn = false
      navigateToHome()
      viewModel.claim(unclaimed.reward.copy(paERewardType = PaERewardType.ROBUX))
      paeAnalytics.sendPaERewardClaimed(
        source = PaEAnalytics.SOURCE_APKFY,
        rewardType = PaERewardType.ROBUX,
        units = unclaimed.reward.units,
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
          rewardAmount = rewardAmount,
          navigateBack = {
            apkfyAnalytics.sendApkfyScreenBackClicked()
            navigateBack()
          },
          onAppClick = { navigateTo(buildAppViewRoute(apkfyData.app)) },
          onCollect = {
            if (rewardState is RewardState.Unclaimed) {
              paeAnalytics.sendPaERewardCardCollectClick(
                source = PaEAnalytics.SOURCE_APKFY,
                rewardType = PaERewardType.ROBUX,
              )
              awaitingSignIn = true
              navigateToSignIn()
            }
          },
          onDismiss = {
            paeAnalytics.sendPaERewardCardDismissClick(
              source = PaEAnalytics.SOURCE_APKFY,
              rewardType = PaERewardType.ROBUX,
            )
            navigateBack()
          },
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
