package cm.aptoide.pt.analytics

import cm.aptoide.pt.feature_categories.analytics.AptoideAnalyticsInfoProvider

class AnalyticsInfoProvider : AptoideAnalyticsInfoProvider {
  override suspend fun getAnalyticsId(): String? = null
}
