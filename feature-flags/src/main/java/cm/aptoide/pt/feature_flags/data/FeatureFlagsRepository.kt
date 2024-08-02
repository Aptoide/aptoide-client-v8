package cm.aptoide.pt.feature_flags.data

import org.json.JSONObject

interface FeatureFlagsRepository {

  suspend fun getFeatureFlags(): JSONObject
}
