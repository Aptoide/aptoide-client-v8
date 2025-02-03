package cm.aptoide.pt.appcomingsoon.repository

import kotlinx.coroutines.flow.Flow

interface SubscribedAppsRepository {
  suspend fun isAppSubscribed(key: String): Flow<Boolean>

  suspend fun saveSubscribedApp(packageName: String, subscribe: Boolean)
}
