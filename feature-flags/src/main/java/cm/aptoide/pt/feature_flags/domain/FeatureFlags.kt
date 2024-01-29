package cm.aptoide.pt.feature_flags.domain

import cm.aptoide.pt.feature_flags.data.FeatureFlagsLocalRepository
import cm.aptoide.pt.feature_flags.data.FeatureFlagsRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeatureFlags @Inject constructor(
  private val settingsRepository: FeatureFlagsRepository,
  private val settingsLocalRepository: FeatureFlagsLocalRepository,
) {

  private var featureFlags = JSONObject()
  private val mutex = Mutex()

  suspend fun initialize() = mutex.withLock {
    featureFlags = try {
      settingsRepository.getFeatureFlags()
        .apply { settingsLocalRepository.saveFeatureFlags(this) }
    } catch (error: Throwable) {
      settingsLocalRepository.getFeatureFlags()
    }
  }

  suspend fun getFlag(key: String): Boolean? = mutex.withLock {
    runCatching { featureFlags.getBoolean(key) }.getOrNull()
  }

  suspend fun getFlag(key: String, default: Boolean): Boolean = mutex.withLock {
    featureFlags.optBoolean(key, default)
  }

  suspend fun getStrings(key: String): List<String> = mutex.withLock {
    featureFlags.optJSONArray(key)?.run {
      val result = mutableListOf<String>()
      val size = length()
      for (i in 0..size) {
        result += optString(i)
      }
      result.filterNot { it.isBlank() }
    } ?: emptyList()
  }
}
