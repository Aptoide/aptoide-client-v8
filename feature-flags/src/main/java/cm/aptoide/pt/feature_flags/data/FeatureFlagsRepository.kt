package cm.aptoide.pt.feature_flags.data

interface FeatureFlagsRepository {

  suspend fun getFeatureFlags(): Map<String, String>
}
