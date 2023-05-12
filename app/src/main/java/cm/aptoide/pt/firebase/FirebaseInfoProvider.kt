package cm.aptoide.pt.firebase

import cm.aptoide.pt.feature_apps.data.network.firebase.AptoideFirebaseInfoProvider

class FirebaseInfoProvider : AptoideFirebaseInfoProvider {
  override suspend fun getFirebaseToken(): String? = null
}
