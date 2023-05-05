package cm.aptoide.pt.feature_categories.analytics

interface AptoideAnalyticsInfoProvider {
  suspend fun getAnalyticsId(): String?
}
