package cm.aptoide.pt.feature_categories.analytics

interface AptoideFirebaseInfoProvider {
  suspend fun getFirebaseToken(): String?
}
