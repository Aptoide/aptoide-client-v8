package com.aptoide.android.aptoidegames.notifications.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.extensions.runPreviewable
import com.aptoide.android.aptoidegames.analytics.AnalyticsSender
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.feature_apps.presentation.AnalyticsInjectionsProvider
import com.aptoide.android.aptoidegames.notifications.analytics.NotificationsAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AnalyticsInjectionsProvider @Inject constructor(
  val genericAnalytics: GenericAnalytics,
) : ViewModel() {
  operator fun component1() = genericAnalytics
}

@Composable
fun rememberNotificationsAnalytics(): NotificationsAnalytics = runPreviewable(
  preview = {
    NotificationsAnalytics(
      GenericAnalytics(object : AnalyticsSender {})
    )
  },
  real = {
    val (genericAnalytics) = hiltViewModel<AnalyticsInjectionsProvider>()
    val notificationsAnalytics = remember(key1 = genericAnalytics) {
      NotificationsAnalytics(
        genericAnalytics = genericAnalytics,
      )
    }

    notificationsAnalytics
  }
)
