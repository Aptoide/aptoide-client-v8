package cm.aptoide.pt.app_games.firebase

import cm.aptoide.pt.feature_categories.analytics.AptoideFirebaseInfoProvider

class FirebaseInfoProvider : AptoideFirebaseInfoProvider {
  override suspend fun getFirebaseToken(): String? = null
}
