package com.aptoide.android.aptoidegames.updates.presentation

import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import cm.aptoide.pt.feature_updates.data.StoreNameProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AptoideUpdatesStoreNameProvider @Inject constructor(
  private val featureFlags: FeatureFlags,
  @StoreName private val defaultStoreName: String,
) : StoreNameProvider {

  override suspend fun getStoreName(): String? {
    val shouldUseDefaultStoreName =
      featureFlags.getFlag("exp10_default_store_name", true)
    return if (shouldUseDefaultStoreName) defaultStoreName else null
  }
}
