package com.aptoide.android.aptoidegames.gamesfeed.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.extensions.runPreviewable
import com.aptoide.android.aptoidegames.analytics.AnalyticsSender
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AnalyticsInjectionsProvider @Inject constructor(
  val genericAnalytics: GenericAnalytics,
) : ViewModel()

@Composable
fun rememberGamesFeedAnalytics(): GamesFeedAnalytics = runPreviewable(
  preview = {
    GamesFeedAnalytics(GenericAnalytics(object : AnalyticsSender {}))
  },
  real = {
    val analyticsProvider = hiltViewModel<AnalyticsInjectionsProvider>()

    val gamesFeedAnalytics by remember(key1 = analyticsProvider.genericAnalytics) {
      derivedStateOf {
        GamesFeedAnalytics(
          genericAnalytics = analyticsProvider.genericAnalytics,
        )
      }
    }
    gamesFeedAnalytics
  }
)
