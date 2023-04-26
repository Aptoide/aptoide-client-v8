package cm.aptoide.pt.analytics

import cm.aptoide.pt.feature_apps.data.network.analytics.AptoideAnalyticsInfoProvider

class AnalyticsInfoProvider : AptoideAnalyticsInfoProvider {
  override suspend fun getAnalyticsId(): String? = null
  override suspend fun getAnalyticsType(): String = ""
}
