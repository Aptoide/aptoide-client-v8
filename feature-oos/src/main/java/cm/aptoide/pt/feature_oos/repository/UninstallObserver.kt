package cm.aptoide.pt.feature_oos.repository

import kotlinx.coroutines.flow.Flow

interface UninstallObserver {

  fun onAppRemoved(): Flow<String>

  suspend fun removedApp(packageName: String)
}
