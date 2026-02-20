package com.aptoide.android.aptoidegames.feature_rtb.presentation

import androidx.compose.runtime.Composable
import cm.aptoide.pt.feature_campaigns.toAptoideMMPCampaign
import com.aptoide.android.aptoidegames.feature_apps.presentation.AppsRowView
import com.aptoide.android.aptoidegames.feature_rtb.data.RTBApp
import com.aptoide.android.aptoidegames.mmp.UTMContext

@Composable
internal fun RTBAppsRowView(
  rtbAppsList: List<RTBApp>,
  navigate: (String) -> Unit,
  onShowLoading: (Boolean) -> Unit = {}
) {
  val handleRTBAdClick = rememberRTBAdClickHandler(
    rtbAppsList = rtbAppsList,
    navigate = navigate,
    onLoadingChange = onShowLoading
  )

  val utmContext = UTMContext.current
  val appsList = rtbAppsList.map { it.app }
  AppsRowView(
    appsList = appsList,
    navigate = navigate,
    onRTBAdClick = handleRTBAdClick,
    onItemVisible = { index ->
      rtbAppsList.getOrNull(index)?.app?.let { app ->
        app.campaigns?.toAptoideMMPCampaign()
          ?.sendImpressionEvent(utmContext, app.packageName)
      }
    },
  )
}
