package com.aptoide.android.aptoidegames.play_and_earn.presentation.analytics

import androidx.compose.runtime.Composable
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
) : ViewModel() {
  operator fun component1() = genericAnalytics
}

@Composable
fun rememberPaEAnalytics(): PaEAnalytics = runPreviewable(
  preview = {
    PaEAnalytics(
      GenericAnalytics(object : AnalyticsSender {})
    )
  },
  real = {
    val (genericAnalytics) = hiltViewModel<AnalyticsInjectionsProvider>()
    val paeAnalytics = remember(key1 = genericAnalytics) {
      PaEAnalytics(
        genericAnalytics = genericAnalytics,
      )
    }

    paeAnalytics
  }
)
