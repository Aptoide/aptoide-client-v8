package cm.aptoide.pt.feature_updates.domain.usecase

import cm.aptoide.pt.install_manager.InstallManager
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class UninstallAppUseCase @Inject constructor(private val installManager: InstallManager) {

  fun uninstallApp(packageName: String) {
    installManager.getApp(packageName).uninstall()
  }
}
