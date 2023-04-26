package cm.aptoide.pt.feature_apps.data.network.analytics

interface AptoideAnalyticsInfoProvider {
  suspend fun getAnalyticsId(): String?
  suspend fun getAnalyticsType(): String
}
