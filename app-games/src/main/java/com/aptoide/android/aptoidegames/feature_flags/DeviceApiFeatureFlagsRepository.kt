package com.aptoide.android.aptoidegames.feature_flags

import cm.aptoide.pt.feature_flags.data.FeatureFlagsRepository
import com.aptoide.android.aptoidegames.feature_flags.analytics.FeatureFlagsAnalytics
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import kotlinx.coroutines.CancellationException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Feature flags from the device API. A failed fetch is reported and rethrown, so the flags domain
 * falls back to its last saved snapshot and the shipped defaults.
 */
class DeviceApiFeatureFlagsRepository(
  private val configApi: ConfigApi,
  private val variant: String,
  private val versionCode: Int,
  private val bucketId: suspend () -> String,
  private val featureFlagsAnalytics: FeatureFlagsAnalytics,
  private val now: () -> Long = System::currentTimeMillis,
) : FeatureFlagsRepository {

  override suspend fun getFeatureFlags(): JSONObject {
    val start = now()
    val flags = try {
      configApi.getConfig(
        variant = variant,
        versionCode = versionCode,
        bucketId = bucketId(),
      ).values
        ?.toFlags()
        // An answer with no values must not replace the snapshot the install already has
        ?.takeIf { it.length() > 0 }
        ?: throw MissingConfigValuesException()
    } catch (e: CancellationException) {
      throw e
    } catch (t: Throwable) {
      featureFlagsAnalytics.sendFeatureFlagsFetchFailure(
        duration = now() - start,
        errorType = t.kind(),
        errorHttpCode = (t as? HttpException)?.code(),
      )
      throw t
    }
    featureFlagsAnalytics.sendFeatureFlagsFetch(duration = now() - start)
    return flags
  }

  // Fixed names: release builds obfuscate the class names of app and library exceptions
  private fun Throwable.kind() = when (this) {
    is HttpException -> "http"
    is MissingConfigValuesException -> "empty_values"
    is JsonParseException -> "parse"
    is SocketTimeoutException -> "timeout"
    is UnknownHostException -> "unknown_host"
    is IOException -> "io"
    else -> "other"
  }

  // The flags domain reads every value as a string, JSON documents included
  private fun JsonObject.toFlags() = JSONObject().apply {
    entrySet().forEach { (key, value) ->
      when {
        value.isJsonNull -> Unit
        value.isJsonPrimitive -> put(key, value.asString)
        else -> put(key, value.toString())
      }
    }
  }

}

/** A successful response with no values in it, which must not replace the snapshot */
class MissingConfigValuesException : IllegalStateException("Config response has no values")
