package cm.aptoide.pt.feature_flags.data

interface FeatureFlagsLocalRepository {

  suspend fun getFeatureFlags(): Map<String, String>
  suspend fun saveFeatureFlags(featureFlags: Map<String, String>)
}
