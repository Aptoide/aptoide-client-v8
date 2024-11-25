package com.aptoide.android.aptoidegames.feature_flags

import cm.aptoide.pt.extensions.SuspendValue
import cm.aptoide.pt.feature_flags.data.FeatureFlagsRepository
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import org.json.JSONObject

class AptoideFeatureFlagsRepository : FeatureFlagsRepository {

  // Used to signal about remote config results
  private var result: SuspendValue<JSONObject> = SuspendValue()

  override suspend fun getFeatureFlags(): JSONObject {
    Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
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
