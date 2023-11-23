package cm.aptoide.pt.feature_updates.domain.usecase

import android.content.pm.PackageManager
import cm.aptoide.pt.extensions.loadIconDrawable
import cm.aptoide.pt.feature_updates.domain.InstalledApp
import cm.aptoide.pt.install_manager.InstallManager
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@ViewModelScoped
class GetInstalledAppsUseCase @Inject constructor(
  private val packageManager: PackageManager,
  private val installManager: InstallManager,
) {

  @OptIn(ExperimentalCoroutinesApi::class)
  suspend fun getInstalledApps(): Flow<List<InstalledApp>> =
    flowOf(installManager.getInstalledApps().toMutableSet())
      .flatMapConcat { set ->
        installManager.getAppsChanges()
          .map { set.apply { add(it) } }
          .onStart { emit(set) }
      }
      .map { set ->
        set.mapNotNull { it.packageInfo.first() }
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
