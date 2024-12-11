package com.aptoide.android.aptoidegames.feature_payments.analytics

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.extensions.runPreviewable
import com.aptoide.android.aptoidegames.analytics.AnalyticsSender
import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AnalyticsInjectionsProvider @Inject constructor(
  val paymentAnalytics: PaymentAnalytics,
) : ViewModel()

@Composable
fun rememberPaymentAnalytics(): PaymentAnalytics = runPreviewable(
  preview = {
    PaymentAnalytics(
      GenericAnalytics(object : AnalyticsSender {}),
      BIAnalytics(object : AnalyticsSender {}),
    )
  },
  real = {
    val vm = hiltViewModel<AnalyticsInjectionsProvider>()
    vm.paymentAnalytics
  }
)
