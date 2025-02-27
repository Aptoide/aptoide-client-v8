package com.aptoide.android.aptoidegames.feature_apps.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.extensions.runPreviewable
import com.aptoide.android.aptoidegames.analytics.AnalyticsSender
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.feature_apps.analytics.BundleAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AnalyticsInjectionsProvider @Inject constructor(
  val genericAnalytics: GenericAnalytics,
) : ViewModel() {
  operator fun component1() = genericAnalytics
}

@Composable
fun rememberBundleAnalytics(): BundleAnalytics = runPreviewable(
  preview = {
    BundleAnalytics(
      GenericAnalytics(object : AnalyticsSender {})
    )
  },
  real = {
    val (genericAnalytics) = hiltViewModel<AnalyticsInjectionsProvider>()
    val bundleAnalytics = remember(key1 = genericAnalytics) {
      BundleAnalytics(
        genericAnalytics = genericAnalytics,
      )
    }

    bundleAnalytics
  }
)
