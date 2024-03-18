package cm.aptoide.pt.app_games.feature_flags

import cm.aptoide.pt.feature_flags.data.FeatureFlagsRepository
import org.json.JSONObject

class AptoideFeatureFlagsRepository : FeatureFlagsRepository {
  override suspend fun getFeatureFlags(): JSONObject = JSONObject()
}
