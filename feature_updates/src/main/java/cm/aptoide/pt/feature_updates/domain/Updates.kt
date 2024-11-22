package cm.aptoide.pt.feature_updates.domain

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.content.pm.PackageInfoCompat
import cm.aptoide.pt.extensions.getInstalledPackages
import cm.aptoide.pt.extensions.getSignature
import cm.aptoide.pt.extensions.ifNormalAppOrGame
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppsListMapper
import cm.aptoide.pt.feature_apps.data.model.AppJSON
import cm.aptoide.pt.feature_updates.data.AutoUpdateWorker
import cm.aptoide.pt.feature_updates.data.UpdatesRepository
import cm.aptoide.pt.feature_updates.data.UpdatesWorker
import cm.aptoide.pt.feature_updates.di.PrioritizedPackagesFilter
import cm.aptoide.pt.feature_updates.presentation.UpdatesNotificationProvider
import cm.aptoide.pt.feature_updates.repository.UpdatesPreferencesRepository
import cm.aptoide.pt.install_info_mapper.domain.InstallPackageInfoMapper
import cm.aptoide.pt.install_manager.InstallManager
import cm.aptoide.pt.install_manager.dto.Constraints
import cm.aptoide.pt.install_manager.dto.Constraints.NetworkType.UNMETERED
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
  private val updatesNotificationBuilder: UpdatesNotificationProvider,
  private val installPackageInfoMapper: InstallPackageInfoMapper,
  private val updatesPreferencesRepository: UpdatesPreferencesRepository,
  @ApplicationContext private val applicationContext: Context,
) {

  val mutex: Mutex = Mutex()

  private var currentUpdates = listOf<AppJSON>()

  init {
    UpdatesWorker.enqueue(applicationContext)
    // TODO: clean this for testability
    CoroutineScope(Dispatchers.IO).launch {
      if (updatesPreferencesRepository.shouldAutoUpdateGames().first())
        AutoUpdateWorker.enqueue(applicationContext)
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
      .filter { it.signature.isNotEmpty() }
    val updates = updatesRepository.loadUpdates(apksData)
    updatesRepository.replaceWith(*updates.toTypedArray())
    if (updates.isNotEmpty()) {
      updatesNotificationBuilder.showUpdatesNotification(updates)
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

  suspend fun autoUpdate() {
    getAppsUpdates()
      .first()
      .filter {
        if (VERSION.SDK_INT >= VERSION_CODES.UPSIDE_DOWN_CAKE) {
          packageManager.getInstallSourceInfo(it.packageName).updateOwnerPackageName == applicationContext.packageName
        } else if (VERSION.SDK_INT >= VERSION_CODES.R) {
          packageManager.getInstallSourceInfo(it.packageName).installingPackageName == applicationContext.packageName
        } else {
          false
        } || it.packageName == applicationContext.packageName
      }
      .forEach {
        installManager.getApp(it.packageName).install(
          installPackageInfo = installPackageInfoMapper.map(it),
          constraints = Constraints(
            checkForFreeSpace = false,
            networkType = UNMETERED
          )
        )
      }
  }
}
