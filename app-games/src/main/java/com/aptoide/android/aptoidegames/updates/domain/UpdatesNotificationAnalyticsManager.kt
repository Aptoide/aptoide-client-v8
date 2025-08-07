package com.aptoide.android.aptoidegames.updates.domain

import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdatesNotificationAnalyticsManager @Inject constructor(
  private val biAnalytics: BIAnalytics,
  private val featureFlags: FeatureFlags
) {

  suspend fun loadUserProperty() {
    val variant = featureFlags.getFlagAsString("updates_notification_type")
    biAnalytics.setUserProperties("experiment7_updates_notification_variant" to variant)
  }
}