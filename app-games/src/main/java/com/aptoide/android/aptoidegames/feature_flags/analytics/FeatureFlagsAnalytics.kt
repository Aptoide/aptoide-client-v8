package com.aptoide.android.aptoidegames.feature_flags.analytics

import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import javax.inject.Inject

class FeatureFlagsAnalytics @Inject constructor(
  private val genericAnalytics: GenericAnalytics,
) {

  fun sendFeatureFlagsFetch(duration: Long) = genericAnalytics.logEvent(
    name = "feature_flags_fetch",
    params = mapOf("duration" to duration)
  )
}
