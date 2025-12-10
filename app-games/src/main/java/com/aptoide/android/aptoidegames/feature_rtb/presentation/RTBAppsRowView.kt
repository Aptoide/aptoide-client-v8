package com.aptoide.android.aptoidegames.feature_rtb.presentation

import androidx.compose.runtime.Composable
import com.aptoide.android.aptoidegames.feature_apps.presentation.AppsRowView
import com.aptoide.android.aptoidegames.feature_rtb.data.RTBApp

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

  val appsList = rtbAppsList.map { it.app }
  AppsRowView(
    appsList = appsList,
    navigate = navigate,
    onRTBAdClick = handleRTBAdClick
  )
}
