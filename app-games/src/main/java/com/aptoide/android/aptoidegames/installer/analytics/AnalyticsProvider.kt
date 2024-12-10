package com.aptoide.android.aptoidegames.installer.analytics

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import com.aptoide.android.aptoidegames.analytics.AnalyticsSender
import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AnalyticsInjectionsProvider @Inject constructor(
  val installAnalytics: InstallAnalytics,
) : ViewModel()

@Composable
fun rememberInstallAnalytics(): InstallAnalytics = runPreviewable(
  preview = {
    InstallAnalytics(
      object : FeatureFlags {},
      GenericAnalytics(object : AnalyticsSender {}),
      BIAnalytics(object : AnalyticsSender {}),
      "",
      object : SilentInstallChecker {}
    )
  },
  real = {
    val vm = hiltViewModel<AnalyticsInjectionsProvider>()
    vm.installAnalytics
  }
)
