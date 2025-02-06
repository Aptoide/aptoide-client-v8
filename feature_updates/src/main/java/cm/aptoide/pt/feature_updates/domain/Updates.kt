package cm.aptoide.pt.feature_updates.domain

import android.content.Context
import android.content.pm.PackageInfo
import cm.aptoide.pt.extensions.compatVersionCode
import cm.aptoide.pt.extensions.getSignature
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppsListMapper
import cm.aptoide.pt.feature_apps.data.model.AppJSON
import cm.aptoide.pt.feature_campaigns.toAptoideMMPCampaign
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.coroutineContext

@Singleton
class Updates @Inject constructor(
  private val updatesRepository: UpdatesRepository,
  private val appsListMapper: AppsListMapper,
  @PrioritizedPackagesFilter private val prioritizedPackages: List<String>,
  private val installManager: InstallManager,
  private val updatesNotificationBuilder: UpdatesNotificationProvider,
  private val installPackageInfoMapper: InstallPackageInfoMapper,
  private val updatesPreferencesRepository: UpdatesPreferencesRepository,
) {

  val mutex: Mutex = Mutex()

  private var currentUpdates = listOf<AppJSON>()

  private lateinit var myPackageName: String

  // TODO: clean this for testability
  suspend fun initialize(context: Context) {
    myPackageName = context.packageName
    installManager.appsChanges
      .onEach { appInstaller ->
        mutex.withLock {
          val packageInfo = appInstaller.packageInfo
          if (packageInfo == null) {
            currentUpdates.find { it.packageName == appInstaller.packageName }
          } else {
            currentUpdates.find {
              it.packageName == appInstaller.packageName
                && it.file.vercode <= packageInfo.compatVersionCode
            }
          }
            ?.also { updatesRepository.remove(it) }
        }
      }
      .launchIn(CoroutineScope(coroutineContext))
    mutex.withLock {
      val installedApps = installManager.installedApps
        .mapNotNull { it.packageInfo }
        .map { it.packageName to it.compatVersionCode }
      val values = updatesRepository.getUpdates().first()
      val toRemove = values.filterNot { update ->
        installedApps.any { it.first == update.packageName && it.second < update.file.vercode }
      }
      currentUpdates = values - toRemove
      updatesRepository.remove(*toRemove.toTypedArray())
    }

    UpdatesWorker.enqueue(context)
    AutoUpdateWorker.enqueue(context)
  }

  val appsUpdates: Flow<List<App>> = updatesRepository.getUpdates()
    .map {
      mutex.withLock { currentUpdates = it }
      it.let(appsListMapper::map)
        .sortedByDescending {
          if (it.packageName in prioritizedPackages) {
            LocalDate.now().plusDays(1).toString()
          } else {
            it.modifiedDate
          }
        }
    }

  suspend fun checkNonUpdatableApps() {
    installManager.installedApps
      .filter { it.updatesOwnerPackageName != myPackageName }
      .mapNotNull { it.packageInfo }
      .also { getUpdates(it) }

    appsUpdates.first().let {
      if (it.isNotEmpty()) {
        updatesNotificationBuilder.showUpdatesNotification(it)
      }
    }
  }

  private suspend fun getUpdates(apps: List<PackageInfo>): List<AppJSON> =
    mutex.withLock {
      val apksData = apps
        .map {
          ApkData(
            signature = it.getSignature()?.uppercase() ?: "",
            packageName = it.packageName,
            versionCode = it.compatVersionCode
          )
        }
        .filter { it.signature.isNotEmpty() }
      val updates = updatesRepository.loadUpdates(apksData)
      updatesRepository.saveOrReplace(*updates.toTypedArray())
      updates
    }

  suspend fun autoUpdate() {
    val shouldAutoUpdateGames = updatesPreferencesRepository.shouldAutoUpdateGames().first()
    val installers = installManager.installedApps
      .filter { it.updatesOwnerPackageName == myPackageName }
    val updates = installers
      .mapNotNull { it.packageInfo }
      .let { getUpdates(it) }
      .let(appsListMapper::map)
      .sortedBy {
        if (it.packageName == myPackageName) {
          LocalDate.now().plusDays(1).toString()
        } else {
          it.modifiedDate
        }
      }
    if (!shouldAutoUpdateGames) {
      return
    }
    installers.forEach { appInstaller ->
      updates
        .firstOrNull { it.packageName == appInstaller.packageName }
        ?.also {
          appInstaller.install(
            installPackageInfo = installPackageInfoMapper.map(it),
            constraints = Constraints(
              checkForFreeSpace = false,
              networkType = UNMETERED
            )
          )

          it.campaigns?.toAptoideMMPCampaign()
            ?.sendDownloadEvent(
              bundleTag = null,
              searchKeyword = null,
              currentScreen = null,
              isCta = false
            )
        }
    }
  }
}
