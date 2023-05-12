package cm.aptoide.pt.feature_apps.data.network.firebase

interface AptoideFirebaseInfoProvider {
  suspend fun getFirebaseToken(): String?
}
