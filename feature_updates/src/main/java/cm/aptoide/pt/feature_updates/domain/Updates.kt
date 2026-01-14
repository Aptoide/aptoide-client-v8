package cm.aptoide.pt.feature_updates.domain

import android.content.Context
import android.content.pm.PackageInfo
import cm.aptoide.pt.extensions.compatVersionCode
import cm.aptoide.pt.extensions.getSignature
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppsListMapper
import cm.aptoide.pt.feature_apps.data.model.AppJSON
import cm.aptoide.pt.feature_campaigns.UTMInfo
import cm.aptoide.pt.feature_campaigns.toAptoideMMPCampaign
import cm.aptoide.pt.feature_campaigns.toMMPLinkerCampaign
import cm.aptoide.pt.feature_updates.data.AutoUpdateWorker
import cm.aptoide.pt.feature_updates.data.UpdatesRepository
import cm.aptoide.pt.feature_updates.data.UpdatesWorker
import cm.aptoide.pt.feature_updates.data.VIPUpdatesProvider
import cm.aptoide.pt.feature_updates.data.VIPUpdatesWorker
import cm.aptoide.pt.feature_updates.di.PrioritizedPackagesFilter
import cm.aptoide.pt.feature_updates.domain.NotificationTypes.AUTO_UPDATE_SUCCESSFUL
import cm.aptoide.pt.feature_updates.domain.NotificationTypes.GENERAL_NOTIFICATION
import cm.aptoide.pt.feature_updates.domain.NotificationTypes.VIP_NOTIFICATION
import cm.aptoide.pt.feature_updates.presentation.UpdatesNotificationProvider
import cm.aptoide.pt.feature_updates.presentation.UpdatesNotificationsVisibilityManager
import cm.aptoide.pt.feature_updates.repository.UpdatesPreferencesRepository
import cm.aptoide.pt.install_info_mapper.domain.InstallPackageInfoMapper
import cm.aptoide.pt.install_manager.InstallManager
import cm.aptoide.pt.install_manager.dto.Constraints
import cm.aptoide.pt.install_manager.dto.Constraints.NetworkType.UNMETERED
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
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
  private val vipUpdatesProvider: VIPUpdatesProvider,
  private val updatesNotificationsVisibilityManager: UpdatesNotificationsVisibilityManager,
  private val autoUpdateGameInstallObserver: AutoUpdateGameInstallObserver
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
        .filterNormalAppsOrGames()
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
    VIPUpdatesWorker.enqueue(context)
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
    val vipPackages = vipUpdatesProvider.getVIPUpdatesList()
    installManager.installedApps
      .filter { it.updatesOwnerPackageName != myPackageName }
      .filter { it.packageName !in vipPackages }
      .filterNormalAppsOrGames()
      .mapNotNull { it.packageInfo }
      .also { getUpdates(it) }

    appsUpdates.first().let {
      if (it.isNotEmpty()) {
        if (updatesNotificationsVisibilityManager.shouldShowNotification(GENERAL_NOTIFICATION)) {
          updatesNotificationBuilder.showUpdatesNotification(it)
        }
      }
    }
  }

  suspend fun checkVIPUpdates(hasPackageInstallsPermission: Boolean) {
    val vipPackages = vipUpdatesProvider.getVIPUpdatesList()
    if (vipPackages.isEmpty()) {
      return
    }

    val savedUpdates = updatesRepository.getUpdates().first()
    val installedAppsToUpdate = installManager.installedApps
      .filter { it.packageName in vipPackages }
      .filterNormalAppsOrGames()
    val updates =
      getUpdates(installedAppsToUpdate.mapNotNull { it.packageInfo }).let(appsListMapper::map)

    val filteredUpdates = savedUpdates.let { savedList ->
      updates.filter { update ->
        val saved = savedList.find { it.packageName == update.packageName }
        saved == null || update.versionCode > saved.file.vercode
      }
    }

    val hasNoRunningDownloads = installManager.workingAppInstallers.firstOrNull() == null
    val shouldAutoUpdateGames = updatesPreferencesRepository.shouldAutoUpdateGames().first()

    val shouldInstall =
      ((shouldAutoUpdateGames == true) && hasNoRunningDownloads && hasPackageInstallsPermission)

    if (shouldInstall && updatesNotificationsVisibilityManager.shouldAutoUpdateGames()) {
      coroutineScope {
        installedAppsToUpdate
          .forEach { appInstaller ->
            filteredUpdates
              .firstOrNull { it.packageName == appInstaller.packageName }
              ?.also {
                if (appInstaller.updatesOwnerPackageName == myPackageName) {
                  appInstaller.install(
                    installPackageInfo = installPackageInfoMapper.map(it),
                    constraints = Constraints(
                      checkForFreeSpace = false,
                      networkType = UNMETERED
                    )
                  )

                  if (updatesNotificationsVisibilityManager.shouldShowNotification(
                      AUTO_UPDATE_SUCCESSFUL
                    )
                  ) {
                    launch {
                      autoUpdateGameInstallObserver.observeInstall(
                        it,
                        appInstaller.packageInfo?.compatVersionCode ?: 0
                      )
                    }
                  }

                  it.campaigns?.run {
                    toAptoideMMPCampaign().sendDownloadEvent(
                      utmInfo = UTMInfo(
                        utmSource = "aptoide",
                        utmContent = "updates"
                      )
                    )

                    toMMPLinkerCampaign().sendDownloadEvent()
                  }
                } else if (updatesNotificationsVisibilityManager.shouldShowNotification(
                    VIP_NOTIFICATION
                  )
                ) {
                  updatesNotificationBuilder.showVIPUpdateNotification(it)
                }

              }
          }
      }
    } else {
      filteredUpdates.forEach {
        if (updatesNotificationsVisibilityManager.shouldShowNotification(VIP_NOTIFICATION)) {
          updatesNotificationBuilder.showVIPUpdateNotification(it)
        }
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
      .filterNormalAppsOrGames()
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
    if (shouldAutoUpdateGames != true || !updatesNotificationsVisibilityManager.shouldAutoUpdateGames()) {
      return
    }

    coroutineScope {
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

            if (updatesNotificationsVisibilityManager.shouldShowNotification(AUTO_UPDATE_SUCCESSFUL)) {
              launch {
                autoUpdateGameInstallObserver.observeInstall(
                  it,
                  appInstaller.packageInfo?.compatVersionCode ?: 0
                )
              }
            }

            it.campaigns?.run {
              toAptoideMMPCampaign().sendDownloadEvent(
                utmInfo = UTMInfo(
                  utmSource = "aptoide",
                  utmContent = "auto-updates"
                )
              )

              toMMPLinkerCampaign().sendDownloadEvent()
            }
          }
      }
    }
  }
}
