package com.aptoide.android.aptoidegames.installer.analytics

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
) : ViewModel() {
  operator fun component1() = genericAnalytics
}

@Composable
fun rememberInstallAnalytics(): InstallAnalytics = runPreviewable(
  preview = {
    InstallAnalytics(
      GenericAnalytics(object : AnalyticsSender {}),
    )
  },
  real = {
    val (genericAnalytics) = hiltViewModel<AnalyticsInjectionsProvider>()

    val installAnalytics by remember(key1 = genericAnalytics) {
      derivedStateOf {
        InstallAnalytics(genericAnalytics = genericAnalytics)
      }
    }
    installAnalytics
  }
)
