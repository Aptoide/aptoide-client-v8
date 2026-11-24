package com.aptoide.android.aptoidegames.feature_flags.analytics

import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.analytics.mapOfNonNull
import javax.inject.Inject

class FeatureFlagsAnalytics @Inject constructor(
  private val genericAnalytics: GenericAnalytics,
) {

  fun sendFeatureFlagsFetch(duration: Long) = genericAnalytics.logEvent(
    name = "feature_flags_fetch",
    params = mapOf("duration" to duration)
  )

  fun sendFeatureFlagsFetchFailure(
    duration: Long,
    errorType: String,
    errorHttpCode: Int?,
  ) = genericAnalytics.logEvent(
    name = "feature_flags_fetch_failure",
    params = mapOfNonNull(
      "duration" to duration,
      "error_type" to errorType,
      "error_http_code" to errorHttpCode,
    )
  )
}
