package com.aptoide.android.aptoidegames.feature_flags

import cm.aptoide.pt.extensions.SuspendValue
import cm.aptoide.pt.feature_flags.data.FeatureFlagsRepository
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.feature_flags.analytics.FeatureFlagsAnalytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import org.json.JSONObject

class AptoideFeatureFlagsRepository(
  private val featureFlagsAnalytics: FeatureFlagsAnalytics
) : FeatureFlagsRepository {

  private var result: SuspendValue<JSONObject> = SuspendValue()

  init {
    Firebase.remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
  }

  override suspend fun getFeatureFlags(): JSONObject {
    val initialTimestamp = System.currentTimeMillis()
    val previousFetchTime = Firebase.remoteConfig.info.fetchTimeMillis

    Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
      if (task.isSuccessful) {
        val currentFetchTime = Firebase.remoteConfig.info.fetchTimeMillis
        val currentTimestamp = System.currentTimeMillis()

        if (currentFetchTime > previousFetchTime) {
          featureFlagsAnalytics.sendFeatureFlagsFetch(duration = currentTimestamp - initialTimestamp)
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
