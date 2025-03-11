package com.aptoide.android.aptoidegames.apkfy.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.extensions.runPreviewable
import com.aptoide.android.aptoidegames.analytics.AnalyticsSender
import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.apkfy.analytics.ApkfyAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AnalyticsInjectionsProvider @Inject constructor(
  val apkfyAnalytics: ApkfyAnalytics
) : ViewModel() {
  operator fun component1() = apkfyAnalytics
}

@Composable
fun rememberApkfyAnalytics(): ApkfyAnalytics = runPreviewable(
  preview = {
    ApkfyAnalytics(
      GenericAnalytics(object : AnalyticsSender {}),
      BIAnalytics(object : AnalyticsSender {}),
      LocalContext.current.applicationContext
    )
  },
  real = {
    val (apkfyAnalytics) = hiltViewModel<AnalyticsInjectionsProvider>()
    apkfyAnalytics
  }
)
