package com.aptoide.android.aptoidegames.feature_flags

import cm.aptoide.pt.extensions.SuspendValue
import cm.aptoide.pt.feature_flags.data.FeatureFlagsRepository
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import org.json.JSONObject

class AptoideFeatureFlagsRepository(
  private val genericAnalytics: GenericAnalytics
) : FeatureFlagsRepository {

  // Used to signal about remote config results
  private var result: SuspendValue<JSONObject> = SuspendValue()

  override suspend fun getFeatureFlags(): JSONObject {
    val initialTimestamp = System.currentTimeMillis()
    val previousFetchTime = Firebase.remoteConfig.info.fetchTimeMillis

    Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
      if (task.isSuccessful) {
        val currentFetchTime = Firebase.remoteConfig.info.fetchTimeMillis
        val currentTimestamp = System.currentTimeMillis()

        if (currentFetchTime > previousFetchTime) {
          genericAnalytics.sendFeatureFlagsFetch(duration = currentTimestamp - initialTimestamp)
        }
      }

      val configFlags = JSONObject().apply {
        Firebase.remoteConfig.all.forEach {
          put(it.key, it.value.asString())
        }
      }

      result.yield(configFlags)
    }

    return result.await()
  }
}
