package com.aptoide.android.aptoidegames.updates.presentation

import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import cm.aptoide.pt.feature_updates.data.VIPUpdatesProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AptoideVIPUpdatesProvider @Inject constructor(
  val featureFlags: FeatureFlags,
) : VIPUpdatesProvider {

  override suspend fun getVIPUpdatesList(): List<String> {
    val vipStringAsJson = featureFlags.getFlagAsString("vip_updates")
    val listType = object : TypeToken<List<String>>() {}.type
    val packagesList: List<String> = runCatching {
      Gson().fromJson(vipStringAsJson, listType) as List<String>
    }.getOrDefault(emptyList())
    return packagesList
  }
}
