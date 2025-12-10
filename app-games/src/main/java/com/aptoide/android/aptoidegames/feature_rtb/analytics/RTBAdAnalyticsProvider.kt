package com.aptoide.android.aptoidegames.feature_rtb.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.extensions.runPreviewable
import com.aptoide.android.aptoidegames.analytics.AnalyticsSender
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.feature_apps.presentation.AnalyticsInjectionsProvider

@Composable
fun rememberRTBAdAnalytics(): RTBAdAnalytics = runPreviewable(
  preview = {
    RTBAdAnalytics(
      GenericAnalytics(object : AnalyticsSender {})
    )
  },
  real = {
    val (genericAnalytics) = hiltViewModel<AnalyticsInjectionsProvider>()
    val rtbAdAnalytics = remember(key1 = genericAnalytics) {
      RTBAdAnalytics(
        genericAnalytics = genericAnalytics,
      )
    }

    rtbAdAnalytics
  }
)
