package cm.aptoide.pt.feature_flags.data

import org.json.JSONObject

interface FeatureFlagsLocalRepository {

  suspend fun getFeatureFlags(): JSONObject
  suspend fun saveFeatureFlags(featureFlags: JSONObject)
}
