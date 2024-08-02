package cm.aptoide.pt.feature_updates.domain.usecase

import android.content.pm.PackageManager
import cm.aptoide.pt.extensions.loadIconDrawable
import cm.aptoide.pt.feature_updates.domain.InstalledApp
import cm.aptoide.pt.install_manager.InstallManager
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@ViewModelScoped
class GetInstalledAppsUseCase @Inject constructor(
  private val packageManager: PackageManager,
  private val installManager: InstallManager,
) {

  @OptIn(ExperimentalCoroutinesApi::class)
  fun getInstalledApps(): Flow<List<InstalledApp>> {
    val apps = installManager.installedApps.toMutableSet()
    return installManager.appsChanges
      .map { apps.apply { add(it) } }
      .onStart { emit(apps) }
      .map { set ->
        set.mapNotNull { it.packageInfo }
          .map {
            InstalledApp(
              appName = it.applicationInfo.loadLabel(packageManager).toString(),
              packageName = it.packageName,
              versionName = it.versionName,
              versionCode = it.versionCode,
              appIcon = it.applicationInfo.loadIconDrawable(packageManager),
            )
          }
          .sortedBy { it.appName.lowercase() }
      }
  }
}
