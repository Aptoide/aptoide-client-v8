package cm.aptoide.pt.feature_oos.domain

import cm.aptoide.pt.extensions.getAppSize
import cm.aptoide.pt.install_manager.InstallManager
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@ViewModelScoped
class InstalledAppsUseCase @Inject constructor(
  private val installManager: InstallManager
) {

  suspend fun getInstalledApps(filterPackages: List<String> = emptyList()): List<String> =
    installManager.getInstalledApps()
      .map { it to (it.packageInfo.first()?.getAppSize() ?: 0) }
      .sortedByDescending { it.second }
      .map { it.first.packageName }
      .filter { !filterPackages.contains(it) }
}
