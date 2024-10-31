package cm.aptoide.pt.feature_updates.domain

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.core.content.pm.PackageInfoCompat
import cm.aptoide.pt.extensions.getInstalledPackages
import cm.aptoide.pt.extensions.getSignature
import cm.aptoide.pt.extensions.ifNormalAppOrGame
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppsListMapper
import cm.aptoide.pt.feature_apps.data.model.AppJSON
import cm.aptoide.pt.feature_updates.data.UpdatesRepository
import cm.aptoide.pt.feature_updates.di.PrioritizedPackagesFilter
import cm.aptoide.pt.feature_updates.presentation.UpdatesNotificationProvider
import cm.aptoide.pt.install_manager.InstallManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.launch
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
  private val updatesNotificationBuilder: UpdatesNotificationProvider
) {

  val mutex: Mutex = Mutex()

  private var currentUpdates = listOf<AppJSON>()

  init {
    // TODO: clean this for testability
    CoroutineScope(Dispatchers.IO).launch {
      installManager.appsChanges
        .collect { appInstaller ->
          mutex.withLock {
            val packageInfo = appInstaller.packageInfo
            if (packageInfo == null) {
              currentUpdates.find { it.packageName == appInstaller.packageName }
            } else {
              currentUpdates.find {
                it.packageName == appInstaller.packageName
                  && it.file.vercode <= PackageInfoCompat.getLongVersionCode(packageInfo)
              }
            }
              ?.also { updatesRepository.remove(it) }
          }
        }
    }
  }

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
    if (updates.isNotEmpty()) {
      updatesNotificationBuilder.showUpdatesNotification(updates.size)
    }
  }

  suspend fun getAppsUpdates(): Flow<List<App>> {
    val installedApps = getInstalledApps()
      .map { it.packageName to PackageInfoCompat.getLongVersionCode(it) }
    return updatesRepository.getUpdates()
      .withIndex()
      .map {
        if (it.index == 0) {
          val toRemove = it.value.filterNot { update ->
            installedApps.any { it.first == update.packageName && it.second < update.file.vercode }
          }
          updatesRepository.remove(*toRemove.toTypedArray())
          it.value - toRemove
        } else {
          it.value
        }
      }
      .map {
        mutex.withLock { currentUpdates = it }
        it.let(appsListMapper::map)
          .sortedBy {
            if (it.packageName in prioritizedPackages) {
              LocalDate.now().plusDays(1).toString()
            } else {
              it.modifiedDate
            }
          }
          .reversed()
      }
  }

  private fun getInstalledApps(): List<PackageInfo> =
    packageManager.getInstalledPackages().filter { it.ifNormalAppOrGame() }
}
