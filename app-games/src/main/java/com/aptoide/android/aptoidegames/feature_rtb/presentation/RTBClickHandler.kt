package com.aptoide.android.aptoidegames.feature_rtb.presentation

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.aptoide.android.aptoidegames.analytics.presentation.withItemPosition
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.feature_rtb.analytics.RTBAdAnalytics
import com.aptoide.android.aptoidegames.feature_rtb.analytics.rememberRTBAdAnalytics
import com.aptoide.android.aptoidegames.feature_rtb.data.RTBApp
import timber.log.Timber

@Composable
fun rememberRTBAdClickHandler(
  rtbAppsList: List<RTBApp>,
  navigate: (String) -> Unit,
  onLoadingChange: (Boolean) -> Unit = {}
): (String, Int) -> Unit {
  val rtbAdAnalytics = rememberRTBAdAnalytics()
  val rtbAdViewModel = rememberRTBAd()
  val context = LocalContext.current

  return { packageName, index ->
    rtbAppsList.getOrNull(index)?.let { rtbApp ->
      handleRTBAdClick(
        rtbApp = rtbApp,
        index = index,
        context = context,
        navigate = navigate,
        onLoadingChange = onLoadingChange,
        rtbAdViewModel = rtbAdViewModel,
        rtbAdAnalytics = rtbAdAnalytics
      )
    }
  }
}

internal fun handleRTBAdClick(
  rtbApp: RTBApp,
  index: Int,
  context: Context,
  navigate: (String) -> Unit,
  onLoadingChange: (Boolean) -> Unit,
  rtbAdViewModel: RTBAdViewModel,
  rtbAdAnalytics: RTBAdAnalytics
) {
  val app = rtbApp.app

  if (rtbApp.adUrl.isNullOrBlank()) {
    navigate(
      buildAppViewRoute(
        appSource = app,
        utmCampaign = app.campaigns?.campaignId
      ).withItemPosition(index)
    )
    return
  }

  if (rtbApp.isAptoideInstall) {
    handleAptoideInstallClick(
      rtbApp = rtbApp,
      index = index,
      navigate = navigate,
      rtbAdViewModel = rtbAdViewModel,
      rtbAdAnalytics = rtbAdAnalytics
    )
  } else {
    handleExternalInstallClick(
      rtbApp = rtbApp,
      index = index,
      context = context,
      navigate = navigate,
      onLoadingChange = onLoadingChange,
      rtbAdViewModel = rtbAdViewModel,
      rtbAdAnalytics = rtbAdAnalytics
    )
  }
}

internal fun handleAptoideInstallClick(
  rtbApp: RTBApp,
  index: Int,
  navigate: (String) -> Unit,
  rtbAdViewModel: RTBAdViewModel,
  rtbAdAnalytics: RTBAdAnalytics
) {
  val app = rtbApp.app

  navigate(
    buildAppViewRoute(
      appSource = app,
      utmCampaign = app.campaigns?.campaignId
    ).withItemPosition(index)
  )

  rtbAdViewModel.onAdCampaignClick(rtbApp.adUrl ?: "") { result ->
    when (result) {
      is AdClickResult.Success -> {
        rtbAdAnalytics.sendRTBAdLoadSuccess(rtbApp.adUrl ?: "", result.finalUrl)
      }

      is AdClickResult.Error -> {
        Timber.e("Ad click error: ${result.message}")
        rtbAdAnalytics.sendRTBAdLoadError(rtbApp.adUrl ?: "", result.message)
      }

      else -> { /* Handle other states if needed */
      }
    }
  }
}

internal fun handleExternalInstallClick(
  rtbApp: RTBApp,
  index: Int,
  context: Context,
  navigate: (String) -> Unit,
  onLoadingChange: (Boolean) -> Unit,
  rtbAdViewModel: RTBAdViewModel,
  rtbAdAnalytics: RTBAdAnalytics
) {
  val app = rtbApp.app
  onLoadingChange(true)

  rtbAdViewModel.onAdCampaignClick(rtbApp.adUrl ?: "") { result ->
    when (result) {
      is AdClickResult.Success -> {
        onLoadingChange(false)
        rtbAdAnalytics.sendRTBAdLoadSuccess(rtbApp.adUrl ?: "", result.finalUrl)
        openGooglePlayUrl(context, result.finalUrl)
      }

      is AdClickResult.Error -> {
        onLoadingChange(false)
        Timber.e("Ad click error: ${result.message}")
        rtbAdAnalytics.sendRTBAdLoadError(rtbApp.adUrl ?: "", result.message)
        navigate(
          buildAppViewRoute(
            appSource = app,
            utmCampaign = app.campaigns?.campaignId
          ).withItemPosition(index)
        )
      }

      else -> {
        onLoadingChange(true)
      }
    }
  }
}

internal fun openGooglePlayUrl(context: Context, url: String) {
  try {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
      setPackage("com.android.vending")
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
    Timber.d("Successfully opened URL: $url")
  } catch (e: Exception) {
    Timber.e(e, "Failed to open URL: $url")
  }
}
