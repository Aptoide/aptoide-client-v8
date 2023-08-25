package cm.aptoide.pt.feature_flags.domain

import cm.aptoide.pt.feature_flags.data.FeatureFlagsLocalRepository
import cm.aptoide.pt.feature_flags.data.FeatureFlagsRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeatureFlags @Inject constructor(
  private val settingsRepository: FeatureFlagsRepository,
  private val settingsLocalRepository: FeatureFlagsLocalRepository,
) {

  private val featureFlags = mutableMapOf<String, String>()
  private val mutex = Mutex()

  suspend fun initialize() = mutex.withLock {
    val featureFlagsResult = try {
      settingsRepository.getFeatureFlags()
        .apply { settingsLocalRepository.saveFeatureFlags(this) }
    } catch (error: Throwable) {
      settingsLocalRepository.getFeatureFlags()
    }
    featureFlags.putAll(featureFlagsResult)
  }

  suspend fun get(key: String): String? = mutex.withLock {
    featureFlags[key]
  }
}
