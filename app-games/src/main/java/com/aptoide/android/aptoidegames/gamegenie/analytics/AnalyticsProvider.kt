package com.aptoide.android.aptoidegames.gamegenie.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.extensions.runPreviewable
import com.aptoide.android.aptoidegames.analytics.AnalyticsSender
import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AnalyticsInjectionsProvider @Inject constructor(
  val biAnalytics: BIAnalytics,
) : ViewModel()

@Composable
fun rememberGameGenieAnalytics(): GameGenieAnalytics = runPreviewable(
  preview = {
    GameGenieAnalytics(BIAnalytics(object : AnalyticsSender {}))
  },
  real = {
    val analyticsProvider = hiltViewModel<AnalyticsInjectionsProvider>()

    val gameGenieAnalytics by remember(key1 = analyticsProvider.biAnalytics) {
      derivedStateOf {
        GameGenieAnalytics(
          biAnalytics = analyticsProvider.biAnalytics,
        )
      }
    }
    gameGenieAnalytics
  }
)
