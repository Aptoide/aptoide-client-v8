package com.aptoide.android.aptoidegames.analytics

import cm.aptoide.pt.feature_categories.analytics.AptoideAnalyticsInfoProvider

class AnalyticsInfoProvider : AptoideAnalyticsInfoProvider {
  override suspend fun getAnalyticsId(): String? = null
}
