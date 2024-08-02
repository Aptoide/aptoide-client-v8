package com.aptoide.android.aptoidegames.analytics.presentation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.extensions.runPreviewable
import com.aptoide.android.aptoidegames.analytics.AnalyticsSender
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InjectionsProvider @Inject constructor(val analytics: GenericAnalytics) : ViewModel()

@Composable
fun rememberGenericAnalytics(): GenericAnalytics = runPreviewable(
  preview = { GenericAnalytics(object : AnalyticsSender {}) },
  real = {
    hiltViewModel<InjectionsProvider>().analytics
  }
)
