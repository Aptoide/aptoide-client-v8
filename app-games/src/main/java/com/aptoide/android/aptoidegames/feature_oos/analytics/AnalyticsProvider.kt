package com.aptoide.android.aptoidegames.feature_oos.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.extensions.runPreviewable
import com.aptoide.android.aptoidegames.analytics.AnalyticsSender
import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AnalyticsInjectionsProvider @Inject constructor(
  val genericAnalytics: GenericAnalytics,
  val biAnalytics: BIAnalytics,
  @StoreName val storeName: String,
) : ViewModel() {
  operator fun component1() = genericAnalytics
  operator fun component2() = biAnalytics
  operator fun component3() = storeName
}

@Composable
fun rememberOosAnalytics(): OutOfSpaceAnalytics = runPreviewable(
  preview = {
    OutOfSpaceAnalytics(
      GenericAnalytics(object : AnalyticsSender {}),
      BIAnalytics(object : AnalyticsSender {}),
      ""
    )
  },
  real = {
    val (genericAnalytics, biAnalytics, storeName) = hiltViewModel<AnalyticsInjectionsProvider>()

    val outOfSpaceAnalytics by remember(key1 = genericAnalytics) {
      derivedStateOf {
        OutOfSpaceAnalytics(
          genericAnalytics = genericAnalytics,
          biAnalytics = biAnalytics,
          storeName = storeName
        )
      }
    }
    outOfSpaceAnalytics
  }
)
