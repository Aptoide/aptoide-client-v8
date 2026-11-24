package com.aptoide.android.aptoidegames.feature_flags

import cm.aptoide.pt.feature_flags.data.FeatureFlagsRepository
import com.aptoide.android.aptoidegames.feature_flags.analytics.FeatureFlagsAnalytics
import com.google.gson.JsonObject
import kotlinx.coroutines.CancellationException
import org.json.JSONObject
import retrofit2.HttpException

/**
 * Feature flags from the device API. A failed fetch is reported and rethrown, so the flags domain
 * falls back to its last saved snapshot and the shipped defaults.
 */
class RemoteConfigFeatureFlagsRepository(
  private val configApi: ConfigApi,
  private val variant: String,
  private val versionCode: Int,
  private val bucketId: suspend () -> String,
  private val featureFlagsAnalytics: FeatureFlagsAnalytics,
  private val now: () -> Long = System::currentTimeMillis,
) : FeatureFlagsRepository {

  override suspend fun getFeatureFlags(): JSONObject {
    val start = now()
    return try {
      val values = configApi.getConfig(
        variant = variant,
        versionCode = versionCode,
        bucketId = bucketId(),
      ).values ?: throw MissingConfigValuesException()
      values.toFlags().also {
        featureFlagsAnalytics.sendFeatureFlagsFetch(duration = now() - start)
      }
    } catch (e: CancellationException) {
      throw e
    } catch (t: Throwable) {
      featureFlagsAnalytics.sendFeatureFlagsFetchFailure(
        duration = now() - start,
        errorType = if (t is HttpException) ERROR_TYPE_HTTP else t.kind(),
        errorHttpCode = (t as? HttpException)?.code(),
      )
      throw t
    }
  }

  private fun Throwable.kind() = javaClass.simpleName.ifEmpty { javaClass.name }

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

  private companion object {
    const val ERROR_TYPE_HTTP = "http"
  }
}

/** A successful response that carries no `values` object, which must not replace the snapshot */
class MissingConfigValuesException : IllegalStateException("Config response has no values")
