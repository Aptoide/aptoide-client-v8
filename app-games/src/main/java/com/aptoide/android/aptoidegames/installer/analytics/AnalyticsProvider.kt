package com.aptoide.android.aptoidegames.installer.analytics

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.extensions.runPreviewable
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AnalyticsInjectionsProvider @Inject constructor(
  val installAnalytics: InstallAnalytics,
) : ViewModel()

@Composable
fun rememberInstallAnalytics(): InstallAnalytics = runPreviewable(
  preview = { object : InstallAnalytics {} },
  real = {
    val vm = hiltViewModel<AnalyticsInjectionsProvider>()
    vm.installAnalytics
  }
)
