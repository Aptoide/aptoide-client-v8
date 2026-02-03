package cm.aptoide.pt.feature_flags.domain

import cm.aptoide.pt.feature_flags.data.FeatureFlagsLocalRepository
import cm.aptoide.pt.feature_flags.data.FeatureFlagsRepository
import com.google.gson.Gson
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

interface FeatureFlags {

  suspend fun initialize() {}

  suspend fun getFlag(key: String): Boolean? = null

  suspend fun getFlag(key: String, default: Boolean): Boolean = default

  suspend fun getFlagAsString(key: String, fallback: String): String = fallback

  suspend fun getFlagAsString(key: String): String? = null

  suspend fun getFlagsAsString(vararg keys: String): Map<String, String?> = emptyMap()

  suspend fun getStrings(key: String): List<String> = emptyList()

  suspend fun <T> getObject(key: String, klass: Class<T>): T? = null

  /**
   * Updates a specific flag in the cache. Used for real-time updates from remote config.
   */
  suspend fun updateFlag(key: String, value: String) {}
}

@Singleton
class FeatureFlagsImpl @Inject constructor(
  private val settingsRepository: FeatureFlagsRepository,
  private val settingsLocalRepository: FeatureFlagsLocalRepository,
) : FeatureFlags {

  private var featureFlags = JSONObject()
  private val mutex = Mutex()

  override suspend fun initialize() = mutex.withLock {
    featureFlags = try {
      settingsRepository.getFeatureFlags()
        .apply { settingsLocalRepository.saveFeatureFlags(this) }
    } catch (_: Throwable) {
      settingsLocalRepository.getFeatureFlags()
    }
  }

  override suspend fun getFlag(key: String): Boolean? = mutex.withLock {
    runCatching { featureFlags.getBoolean(key) }.getOrNull()
  }

  override suspend fun getFlag(key: String, default: Boolean): Boolean = mutex.withLock {
    featureFlags.optBoolean(key, default)
  }

  override suspend fun getFlagAsString(key: String, fallback: String): String = mutex.withLock {
    featureFlags.optString(key, fallback)
  }

  override suspend fun getFlagAsString(key: String): String? = mutex.withLock {
    runCatching { featureFlags.getString(key) }.getOrNull()
  }

  override suspend fun getFlagsAsString(vararg keys: String) = mutex.withLock {
    keys.associateWith {
      runCatching { featureFlags.getString(it) }.getOrNull()
    }
  }

  override suspend fun getStrings(key: String): List<String> = mutex.withLock {
    featureFlags.optJSONArray(key)?.run {
      val result = mutableListOf<String>()
      val size = length()
      for (i in 0..size) {
        result += optString(i)
      }
      result.filterNot { it.isBlank() }
    } ?: emptyList()
  }

  override suspend fun <T> getObject(key: String, klass: Class<T>) = mutex.withLock {
    runCatching {
      val jsonStr = featureFlags.getJSONObject(key).toString()
      Gson().fromJson(jsonStr, klass)
    }.getOrNull()
  }

  override suspend fun updateFlag(key: String, value: String) {
    mutex.withLock {
      featureFlags.put(key, value)
      settingsLocalRepository.saveFeatureFlags(featureFlags)
    }
  }
}
