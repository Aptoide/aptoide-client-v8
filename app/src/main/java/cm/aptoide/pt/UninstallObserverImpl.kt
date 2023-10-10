package cm.aptoide.pt

import cm.aptoide.pt.feature_oos.repository.UninstallObserver
import kotlinx.coroutines.flow.Flow

class UninstallObserverImpl : UninstallObserver {
  override fun onAppRemoved(): Flow<String> {
    TODO("Not yet implemented")
  }

  override suspend fun removedApp(packageName: String) {
    TODO("Not yet implemented")
  }
}
