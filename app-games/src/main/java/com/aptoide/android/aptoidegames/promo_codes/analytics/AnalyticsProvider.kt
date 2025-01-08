package com.aptoide.android.aptoidegames.promo_codes.analytics

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.extensions.runPreviewable
import com.aptoide.android.aptoidegames.analytics.AnalyticsSender
import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AnalyticsInjectionsProvider @Inject constructor(
  val promoCodeAnalytics: PromoCodeAnalytics,
) : ViewModel()

@Composable
fun rememberPromoCodeAnalytics(): PromoCodeAnalytics = runPreviewable(
  preview = {
    PromoCodeAnalytics(
      biAnalytics = BIAnalytics(object : AnalyticsSender {}),
    )
  },
  real = {
    val vm = hiltViewModel<AnalyticsInjectionsProvider>()
    vm.promoCodeAnalytics
  }
)
