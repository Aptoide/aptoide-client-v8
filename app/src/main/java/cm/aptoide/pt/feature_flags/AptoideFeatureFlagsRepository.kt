package cm.aptoide.pt.feature_flags

import cm.aptoide.pt.feature_flags.data.FeatureFlagsRepository

class AptoideFeatureFlagsRepository : FeatureFlagsRepository {

  override suspend fun getFeatureFlags(): Map<String, String> =
    emptyMap()
}
