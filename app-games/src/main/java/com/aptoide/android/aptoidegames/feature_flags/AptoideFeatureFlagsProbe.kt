package com.aptoide.android.aptoidegames.feature_flags

import cm.aptoide.pt.feature_flags.data.FeatureFlagsRepository
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import org.json.JSONObject

class AptoideFeatureFlagsProbe(
  private val aptoideFeatureFlagsRepository: AptoideFeatureFlagsRepository,
  private val genericAnalytics: GenericAnalytics
) : FeatureFlagsRepository {
  override suspend fun getFeatureFlags(): JSONObject {
    val initialTimestamp = System.currentTimeMillis()
    return aptoideFeatureFlagsRepository.getFeatureFlags().also {
      val currentTimestamp = System.currentTimeMillis()
      genericAnalytics.sendFeatureFlagsFetch(duration = currentTimestamp - initialTimestamp)
    }
  }
}
