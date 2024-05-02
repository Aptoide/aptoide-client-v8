package com.aptoide.android.aptoidegames.feature_flags

import cm.aptoide.pt.feature_flags.data.FeatureFlagsRepository
import org.json.JSONObject

class AptoideFeatureFlagsRepository : FeatureFlagsRepository {
  override suspend fun getFeatureFlags(): JSONObject = JSONObject()
}
