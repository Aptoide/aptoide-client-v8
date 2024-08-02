package com.aptoide.android.aptoidegames.analytics

import cm.aptoide.pt.feature_categories.analytics.AptoideAnalyticsInfoProvider
import com.google.firebase.installations.FirebaseInstallations
import kotlinx.coroutines.tasks.await

class AnalyticsInfoProvider(
  private val firebaseInstallations: FirebaseInstallations,
) : AptoideAnalyticsInfoProvider {

  private var analyticsId: String? = null

  override suspend fun getAnalyticsId(): String? {
    if (analyticsId == null)
      analyticsId = runCatching { firebaseInstallations.id.await() }.getOrNull()
    return analyticsId
  }
}
