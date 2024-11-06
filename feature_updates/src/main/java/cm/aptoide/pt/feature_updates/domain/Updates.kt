package cm.aptoide.pt.feature_updates.domain

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.core.content.pm.PackageInfoCompat
import cm.aptoide.pt.extensions.getInstalledPackages
import cm.aptoide.pt.extensions.getSignature
import cm.aptoide.pt.extensions.ifNormalAppOrGame
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppsListMapper
import cm.aptoide.pt.feature_updates.data.UpdatesRepository
import cm.aptoide.pt.feature_updates.di.PrioritizedPackagesFilter
import cm.aptoide.pt.install_manager.InstallManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Updates @Inject constructor(
  private val packageManager: PackageManager,
  private val updatesRepository: UpdatesRepository,
  private val appsListMapper: AppsListMapper,
  @PrioritizedPackagesFilter private val prioritizedPackages: List<String>,
  private val installManager: InstallManager,
) {

  val mutex: Mutex = Mutex()

  suspend fun check() = mutex.withLock {
    val apksData = getInstalledApps()
      .map {
        ApkData(
          signature = packageManager.getSignature(it.packageName).uppercase(),
          packageName = it.packageName,
          versionCode = PackageInfoCompat.getLongVersionCode(it)
        )
      }
    val updates = updatesRepository.loadUpdates(apksData)
    updatesRepository.replaceWith(*updates.toTypedArray())
  }

  suspend fun getAppsUpdates(): Flow<List<App>> {
    val data = mutex.withLock {
      updatesRepository.getUpdates()
        .let(appsListMapper::map)
        .sortedBy {
          if (it.packageName in prioritizedPackages) {
            LocalDate.now().plusDays(1).toString()
          } else {
            it.modifiedDate
          }
        }
        .reversed()
    }
    val currentList = getInstalledApps()
    val filteredList = data.mapNotNull { app ->
      currentList.firstOrNull { it.packageName == app.packageName }
        ?.let { PackageInfoCompat.getLongVersionCode(it) }
        ?.takeIf { it < app.versionCode }
        ?.let { app }
    }.toMutableList()
    return installManager.appsChanges
      .map { appInstaller ->
        val packageInfo = appInstaller.packageInfo
        if (packageInfo == null) {
          filteredList.removeIf { it.packageName == appInstaller.packageName }
        } else {
          filteredList.removeIf {
            it.packageName == packageInfo.packageName
              && it.versionCode <= PackageInfoCompat.getLongVersionCode(packageInfo)
          }
        }
        filteredList
      }
      .onStart { emit(filteredList) }
  }

  private fun getInstalledApps(): List<PackageInfo> =
    packageManager.getInstalledPackages().filter { it.ifNormalAppOrGame() }
}
