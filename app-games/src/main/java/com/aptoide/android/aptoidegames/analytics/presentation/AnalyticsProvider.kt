package com.aptoide.android.aptoidegames.analytics.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.extensions.runPreviewable
import com.aptoide.android.aptoidegames.analytics.AnalyticsSender
import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.analytics.GeneralAnalytics
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val genericAnalytics: GenericAnalytics,
  val biAnalytics: BIAnalytics,
) : ViewModel()

@Composable
fun rememberGeneralAnalytics(): GeneralAnalytics = runPreviewable(
  preview = {
    GeneralAnalytics(
      GenericAnalytics(object : AnalyticsSender {}),
    )
  },
  real = {
    val analyticsProvider = hiltViewModel<InjectionsProvider>()
    val generalAnalytics = remember(key1 = analyticsProvider.genericAnalytics) {
      GeneralAnalytics(
        genericAnalytics = analyticsProvider.genericAnalytics,
      )
    }

    generalAnalytics
  }
)
