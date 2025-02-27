package com.aptoide.android.aptoidegames.categories.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.extensions.runPreviewable
import com.aptoide.android.aptoidegames.analytics.AnalyticsSender
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.categories.analytics.CategoriesAnalytics
import com.aptoide.android.aptoidegames.feature_apps.presentation.AnalyticsInjectionsProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AnalyticsInjectionsProvider @Inject constructor(
  val genericAnalytics: GenericAnalytics,
) : ViewModel() {
  operator fun component1() = genericAnalytics
}

@Composable
fun rememberCategoriesAnalytics(): CategoriesAnalytics = runPreviewable(
  preview = {
    CategoriesAnalytics(
      GenericAnalytics(object : AnalyticsSender {})
    )
  },
  real = {
    val (genericAnalytics) = hiltViewModel<AnalyticsInjectionsProvider>()
    val categoriesAnalytics = remember(key1 = genericAnalytics) {
      CategoriesAnalytics(
        genericAnalytics = genericAnalytics,
      )
    }

    categoriesAnalytics
  }
)
