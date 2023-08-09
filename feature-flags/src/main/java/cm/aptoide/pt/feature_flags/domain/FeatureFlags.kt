package cm.aptoide.pt.feature_flags.domain

import cm.aptoide.pt.aptoide_network.domain.Blocker
import cm.aptoide.pt.feature_flags.data.FeatureFlagsLocalRepository
import cm.aptoide.pt.feature_flags.data.FeatureFlagsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeatureFlags @Inject constructor(
  private val settingsRepository: FeatureFlagsRepository,
  private val settingsLocalRepository: FeatureFlagsLocalRepository,
) {

  private val featureFlags = mutableMapOf<String, String>()
  private var blocker: Blocker? = null

  suspend fun initialize() {
    blocker?.await()
    Blocker().also {
      blocker = it
      val featureFlagsResult = try {
        settingsRepository.getFeatureFlags()
          .apply { settingsLocalRepository.saveFeatureFlags(this) }
      } catch (error: Throwable) {
        settingsLocalRepository.getFeatureFlags()
      } finally {
        blocker = null
        it.yield()
      }
      featureFlags.putAll(featureFlagsResult)
    }
  }

  suspend fun get(key: String): String? {
    blocker?.await()
    return featureFlags[key]
  }
}
