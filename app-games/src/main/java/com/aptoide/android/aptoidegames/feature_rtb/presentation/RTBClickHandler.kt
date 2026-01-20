package com.aptoide.android.aptoidegames.feature_rtb.presentation

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.aptoide.android.aptoidegames.analytics.presentation.withItemPosition
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
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
  val context = LocalContext.current

  return { packageName, index ->
    rtbAppsList.getOrNull(index)?.let { rtbApp ->
      if (rtbApp.adUrl.isNullOrBlank()) {
        navigateToAppView(navigate, rtbApp.app, index)
        return@let
      }

      onLoadingChange(true)

      val callback: (AdRedirectWebViewActivity.AdRedirectResult) -> Unit = { result ->
        onLoadingChange(false)
        when (result) {
          is AdRedirectWebViewActivity.AdRedirectResult.Success -> {
            Timber.d("Success! Opening Google Play with URL: ${result.finalUrl}")
            rtbAdAnalytics.sendRTBAdLoadSuccess(
              initialUrl = rtbApp.adUrl,
              finalUrl = result.finalUrl,
              campaignId = rtbApp.app.campaigns?.campaignId
            )
            openGooglePlayUrl(context, result.finalUrl)
          }

          is AdRedirectWebViewActivity.AdRedirectResult.Error -> {
            Timber.d("Error: ${result.message}, lastUrl: ${result.lastUrl}, errorType: ${result.lastErrorType}, errorDescription: ${result.lastErrorDescription}")
            rtbAdAnalytics.sendRTBAdLoadError(
              initialUrl = rtbApp.adUrl,
              errorMessage = result.message,
              campaignId = rtbApp.app.campaigns?.campaignId,
              lastUrl = result.lastUrl,
              lastErrorType = result.lastErrorType,
              lastErrorDescription = result.lastErrorDescription
            )
            navigateToAppView(navigate, rtbApp.app, index)
          }
        }
      }

      val intent = AdRedirectWebViewActivity.createIntent(
        context = context,
        trackingUrl = rtbApp.adUrl,
        campaignId = rtbApp.app.campaigns?.campaignId,
        timeoutSeconds = rtbApp.adTimeout,
        callback = callback
      )
      context.startActivity(intent)
    }
  }
}

private fun navigateToAppView(
  navigate: (String) -> Unit,
  app: cm.aptoide.pt.feature_apps.data.App,
  index: Int
) {
  navigate(
    buildAppViewRoute(
      appSource = app,
      utmCampaign = app.campaigns?.campaignId
    ).withItemPosition(index)
  )
}

internal fun openGooglePlayUrl(context: Context, url: String) {
  try {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
      setPackage("com.android.vending")
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
  } catch (e: Exception) {
    Timber.e(e, "Failed to open URL: $url")
    try {
      val browserIntent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(browserIntent)
    } catch (e2: Exception) {
      Timber.e(e2, "Failed to open URL in browser: $url")
    }
  }
}
