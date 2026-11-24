package cm.aptoide.pt.feature_flags.domain

import cm.aptoide.pt.feature_flags.data.FeatureFlagsLocalRepository
import cm.aptoide.pt.feature_flags.data.FeatureFlagsRepository
import com.google.gson.Gson
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONArray
import org.json.JSONObject
import java.util.Optional
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

  suspend fun getStringList(key: String): List<String> = emptyList()

  /**
   * Like [getStringList] but returns null when the key is missing or the value
   * cannot be parsed, so callers can distinguish "key absent" from "empty list".
   */
  suspend fun getStringListOrNull(key: String): List<String>? = null

  suspend fun getInt(key: String, default: Int): Int = default

  suspend fun <T> getObject(key: String, klass: Class<T>): T? = null
}

@Singleton
class FeatureFlagsImpl @Inject constructor(
  private val settingsRepository: FeatureFlagsRepository,
  private val settingsLocalRepository: FeatureFlagsLocalRepository,
  private val defaults: Optional<FeatureFlagsDefaults>,
) : FeatureFlags {

  private var featureFlags = JSONObject()
  private val mutex = Mutex()

  /**
   * With a snapshot from an earlier run the flags are readable straight away (shipped defaults
   * under the snapshot) and the fetched values replace them when the fetch lands. Without one,
   * on a first run, readers wait for the fetch so they do not act on the bare defaults.
   * A failed fetch leaves the snapshot, or the defaults alone, in place.
   */
  override suspend fun initialize() {
    val shipped = shippedDefaults()
    val fetchPending = mutex.withLock {
      val snapshot = settingsLocalRepository.getFeatureFlags()
      val hasSnapshot = snapshot.length() > 0
      featureFlags = shipped.overlaidWith(if (hasSnapshot) snapshot else fetchAndSave())
      hasSnapshot
    }
    if (fetchPending) {
      fetchAndSave()?.let { fetched ->
        mutex.withLock { featureFlags = shipped.overlaidWith(fetched) }
      }
    }
  }

  // The snapshot holds remote values only, so the defaults always come from the running build
  private suspend fun fetchAndSave(): JSONObject? = try {
    settingsRepository.getFeatureFlags()
      .apply { settingsLocalRepository.saveFeatureFlags(this) }
  } catch (_: Throwable) {
    null
  }

  private fun shippedDefaults(): JSONObject =
    runCatching { defaults.map { it.get() }.orElse(null) }.getOrNull() ?: JSONObject()

  private fun JSONObject.overlaidWith(values: JSONObject?) = JSONObject(toString()).apply {
    values?.keys()?.forEach { put(it, values.get(it)) }
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

  /**
   * Alternative to [getStrings] that works when flag values are stored as plain strings.
   * Unlike [getStrings], which uses `optJSONArray` and expects a native JSONArray type,
   * this parses the string value as a JSON array, handling the string-encoded format.
   */
  override suspend fun getStringList(key: String): List<String> = mutex.withLock {
    runCatching {
      JSONArray(featureFlags.getString(key)).run {
        val result = mutableListOf<String>()
        val size = length()
        for (i in 0 until size) {
          result += optString(i)
        }
        result.filterNot { it.isBlank() }
      }
    }.getOrNull() ?: emptyList()
  }

  override suspend fun getStringListOrNull(key: String): List<String>? = mutex.withLock {
    runCatching {
      val raw = featureFlags.getString(key)
      JSONArray(raw).let { array ->
        (0 until array.length()).mapNotNull { i ->
          array.optString(i).takeIf { it.isNotBlank() }
        }
      }
    }.getOrNull()
  }

  override suspend fun getInt(key: String, default: Int): Int = mutex.withLock {
    featureFlags.optString(key, default.toString()).toIntOrNull() ?: default
  }

  override suspend fun <T> getObject(key: String, klass: Class<T>) = mutex.withLock {
    runCatching {
      // Remote values and shipped defaults are stored here as strings, even when the flag
      // itself represents a JSON object - so this can't assume the stored value is already a
      // JSONObject the way featureFlags.getJSONObject(key) does.
      val jsonStr = when (val raw = featureFlags.opt(key)) {
        is JSONObject -> raw.toString()
        is String -> raw
        else -> return@runCatching null
      }
      Gson().fromJson(jsonStr, klass)
    }.getOrNull()
  }
}
