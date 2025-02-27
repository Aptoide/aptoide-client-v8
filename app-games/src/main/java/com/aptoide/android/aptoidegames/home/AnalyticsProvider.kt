package com.aptoide.android.aptoidegames.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.extensions.runPreviewable
import com.aptoide.android.aptoidegames.analytics.AnalyticsSender
import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.home.analytics.HomeAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AnalyticsInjectionsProvider @Inject constructor(
  val genericAnalytics: GenericAnalytics,
  val biAnalytics: BIAnalytics,
) : ViewModel() {
  operator fun component1() = genericAnalytics
  operator fun component2() = biAnalytics
}

@Composable
fun rememberHomeAnalytics(): HomeAnalytics = runPreviewable(
  preview = {
    HomeAnalytics(
      GenericAnalytics(object : AnalyticsSender {}),
      BIAnalytics(object : AnalyticsSender {}),
    )
  },
  real = {
    val (genericAnalytics, biAnalytics) = hiltViewModel<AnalyticsInjectionsProvider>()
    val homeAnalytics = remember(key1 = genericAnalytics) {
      HomeAnalytics(
        genericAnalytics = genericAnalytics,
        biAnalytics = biAnalytics
      )
    }

    homeAnalytics
  }
)
