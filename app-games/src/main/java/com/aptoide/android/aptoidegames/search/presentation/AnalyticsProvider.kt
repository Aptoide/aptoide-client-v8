package com.aptoide.android.aptoidegames.search.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.extensions.runPreviewable
import com.aptoide.android.aptoidegames.analytics.AnalyticsSender
import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.search.analytics.SearchAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val biAnalytics: BIAnalytics,
  val genericAnalytics: GenericAnalytics,
) : ViewModel() {
  operator fun component1() = biAnalytics
  operator fun component2() = genericAnalytics
}

@Composable
fun rememberSearchAnalytics(): SearchAnalytics = runPreviewable(
  preview = {
    SearchAnalytics(
      BIAnalytics(object : AnalyticsSender {}),
      GenericAnalytics(object : AnalyticsSender {})
    )
  },
  real = {
    val (biAnalytics, genericAnalytics) = hiltViewModel<InjectionsProvider>()

    val searchAnalytics = remember(
      key1 = biAnalytics,
      key2 = genericAnalytics
    ) {
      SearchAnalytics(
        biAnalytics = biAnalytics,
        genericAnalytics = genericAnalytics
      )
    }
    searchAnalytics
  }
)
