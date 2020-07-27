package cm.aptoide.pt.download.view

import android.content.Context
import android.content.pm.PackageManager
import cm.aptoide.analytics.AnalyticsManager
import cm.aptoide.pt.actions.PermissionManager
import cm.aptoide.pt.actions.PermissionService
import cm.aptoide.pt.ads.MoPubAdsManager
import cm.aptoide.pt.ads.WalletAdsOfferManager.OfferResponseStatus
import cm.aptoide.pt.app.migration.AppcMigrationManager
import cm.aptoide.pt.crashreports.CrashReport
import cm.aptoide.pt.database.room.RoomDownload
import cm.aptoide.pt.download.*
import cm.aptoide.pt.install.InstallAnalytics
import cm.aptoide.pt.install.InstallManager
import cm.aptoide.pt.notification.NotificationAnalytics
import cm.aptoide.pt.presenter.SubListPresenter
import cm.aptoide.pt.presenter.View
import cm.aptoide.pt.themes.ThemeManager
import rx.Completable
import rx.Observable
import rx.Scheduler

/**
 * This presenter is only responsible for handling download actions.
 * This means that whoever uses this is responsible for updating the download status correctly
 */
class DownloadViewActionPresenter(val installManager: InstallManager,
                                  val moPubAdsManager: MoPubAdsManager,
                                  val permissionManager: PermissionManager,
                                  val appcMigrationManager: AppcMigrationManager,
                                  val downloadDialogManager: DownloadDialogManager,
                                  val permissionService: PermissionService,
                                  val ioScheduler: Scheduler,
                                  val viewScheduler: Scheduler,
                                  val downloadFactory: DownloadFactory,
                                  val downloadAnalytics: DownloadAnalytics,
                                  val installAnalytics: InstallAnalytics,
                                  val notificationAnalytics: NotificationAnalytics,
                                  val packageManager: PackageManager,
                                  val themeManager: ThemeManager,
                                  val context: Context,
                                  val crashReport: CrashReport) :
    SubListPresenter<DownloadClick>() {

  private lateinit var analyticsContext: DownloadAnalytics.AppContext
  private var isInApkfyContext = false
  private var editorsChoicePosition: String? = null

  fun setContextParams(context: DownloadAnalytics.AppContext, isApkfy: Boolean,
                       editorsChoicePosition: String?) {
    this.analyticsContext = context
    this.isInApkfyContext = isApkfy
    this.editorsChoicePosition = editorsChoicePosition
  }

  override fun present() {
    if (!this::analyticsContext.isInitialized) {
      throw java.lang.IllegalStateException("setContextParams must be called!")
    }
    lifecycleView.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .flatMap {
          eventObservable
              .flatMapCompletable { event ->
                when (event.action) {
                  DownloadEvent.INSTALL -> installApp(event)
                  DownloadEvent.RESUME -> resumeDownload(event)
                  DownloadEvent.PAUSE -> pauseDownload(event)
                  DownloadEvent.CANCEL -> cancelDownload(event)
                }
              }
              .retry()
        }
        .compose(lifecycleView.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { err -> crashReport.log(err) })

  }

  private fun installApp(downloadClick: DownloadClick): Completable {
    when (downloadClick.download.getDownloadModel()?.action) {
      DownloadStatusModel.Action.MIGRATE,
      DownloadStatusModel.Action.UPDATE,
      DownloadStatusModel.Action.INSTALL -> {
        return moPubAdsManager.adsVisibilityStatus
            .flatMapCompletable { status -> downloadApp(downloadClick.download, status) }
      }
      DownloadStatusModel.Action.DOWNGRADE -> {
        return moPubAdsManager.adsVisibilityStatus
            .flatMapCompletable { status -> downgradeApp(downloadClick.download, status) }
      }
      DownloadStatusModel.Action.OPEN -> {
        return downloadDialogManager.openApp(downloadClick.download.getPackageName())
      }
      else -> {
        return Completable.complete()
      }
    }
  }

  private fun downgradeApp(download: Download,
                           status: OfferResponseStatus): Completable {
    return downloadDialogManager.showDowngradeDialog()
        .filter { downgrade -> downgrade }
        .doOnNext { downloadDialogManager.showDowngradingSnackBar() }
        .flatMapCompletable { downloadApp(download, status) }
        .toCompletable()
  }

  private fun downloadApp(download: Download,
                          status: OfferResponseStatus): Completable {
    return Observable.defer {
      if (installManager.showWarning()) {
        return@defer downloadDialogManager.showRootInstallWarningPopup()
            .doOnNext { answer -> installManager.rootInstallAllowed(answer) }
            .map { download }
      }
      return@defer Observable.just(download)
    }
        .observeOn(viewScheduler)
        .flatMap {
          permissionManager.requestDownloadAccessWithWifiBypass(permissionService,
              download.getSize())
              .flatMap { permissionManager.requestExternalStoragePermission(permissionService) }
              .observeOn(ioScheduler)
              .flatMapCompletable {
                createDownload(download, status)
                    .doOnNext { roomDownload ->
                      setupDownloadEvents(roomDownload, download.getAppId(),
                          download.getDownloadModel()!!.action, status, download.getStoreName(),
                          download.getMalware().rank.name)
                      if (DownloadStatusModel.Action.MIGRATE == download.getDownloadModel()!!.action) {
                        installAnalytics.uninstallStarted(download.getPackageName(),
                            AnalyticsManager.Action.INSTALL,
                            analyticsContext)
                        appcMigrationManager.addMigrationCandidate(download.getPackageName())
                      }
                    }
                    .flatMapCompletable { download -> installManager.install(download) }
                    .toCompletable()
              }

        }
        .toCompletable()

  }

  private fun createDownload(download: Download,
                             offerResponseStatus: OfferResponseStatus): Observable<RoomDownload> {
    return Observable.just(
        downloadFactory.create(
            parseDownloadAction(download.getDownloadModel()!!.action),
            download.getAppName(), download.getPackageName(), download.getMd5(), download.getIcon(),
            download.getVersionName(), download.getVersionCode(), download.getPath(),
            download.getPathAlt(),
            download.getObb(), download.hasAdvertising() || download.hasBilling(),
            download.getSize(),
            download.getSplits(), download.getRequiredSplits(),
            download.getMalware().rank.toString(), download.getStoreName(), download.getOemId()))
        .doOnError { throwable ->
          if (throwable is InvalidAppException) {
            downloadAnalytics.sendAppNotValidError(download.getPackageName(),
                mapDownloadAction(download.getDownloadModel()!!.action), offerResponseStatus,
                download.getDownloadModel()!!.action == DownloadStatusModel.Action.MIGRATE,
                download.getSplits().isNotEmpty(),
                download.hasAdvertising() || download.hasBilling(), download.getMalware()
                .rank
                .toString(), download.getStoreName(), isInApkfyContext, throwable)
          }
        }
  }

  private fun resumeDownload(downloadClick: DownloadClick): Completable {
    return installManager.getDownload(downloadClick.download.getMd5())
        .flatMap { download ->
          moPubAdsManager.adsVisibilityStatus
              .doOnSuccess { status ->
                val dl = downloadClick.download
                setupDownloadEvents(download, dl.getAppId(), dl.getDownloadModel()!!.action, status,
                    dl.getStoreName(), dl.getMalware().rank.name)
              }.map { download }
        }
        .doOnError { throwable -> throwable.printStackTrace() }
        .flatMapCompletable { download -> installManager.install(download) }
  }

  private fun pauseDownload(downloadClick: DownloadClick): Completable {
    return Completable.fromAction {
      downloadAnalytics.downloadInteractEvent(downloadClick.download.getPackageName(), "pause")
    }.andThen(installManager.pauseInstall(downloadClick.download.getMd5()))
  }

  private fun cancelDownload(downloadClick: DownloadClick): Completable {
    return Completable.fromAction {
      downloadAnalytics.downloadInteractEvent(downloadClick.download.getPackageName(), "cancel")
    }.andThen(installManager.cancelInstall(downloadClick.download.getMd5(),
        downloadClick.download.getPackageName(), downloadClick.download.getVersionCode()))
  }


  fun setupDownloadEvents(download: RoomDownload,
                          appId: Long,
                          downloadAction: DownloadStatusModel.Action,
                          offerResponseStatus: OfferResponseStatus?,
                          storeName: String?, malwareRank: String) {
    val campaignId = notificationAnalytics.getCampaignId(download.packageName, appId)
    val abTestGroup = notificationAnalytics.getAbTestingGroup(download.packageName, appId)
    installAnalytics.installStarted(download.packageName, download.versionCode,
        AnalyticsManager.Action.INSTALL, analyticsContext,
        getOrigin(download.action), campaignId, abTestGroup,
        downloadAction == DownloadStatusModel.Action.MIGRATE,
        download.hasAppc(), download.hasSplits(), offerResponseStatus.toString(), malwareRank,
        storeName, isInApkfyContext)
    if (DownloadStatusModel.Action.MIGRATE == downloadAction) {
      downloadAnalytics.migrationClicked(download.md5, download.packageName, malwareRank,
          editorsChoicePosition, InstallType.UPDATE_TO_APPC, AnalyticsManager.Action.INSTALL,
          offerResponseStatus,
          download.hasAppc(), download.hasSplits(), storeName, isInApkfyContext)
      downloadAnalytics.downloadStartEvent(download, campaignId, abTestGroup,
          analyticsContext, AnalyticsManager.Action.INSTALL, true, isInApkfyContext)
    } else {
      downloadAnalytics.installClicked(download.md5, download.packageName, malwareRank,
          editorsChoicePosition, mapDownloadAction(downloadAction), AnalyticsManager.Action.INSTALL,
          offerResponseStatus,
          download.hasAppc(), download.hasSplits(), storeName, isInApkfyContext)
      downloadAnalytics.downloadStartEvent(download, campaignId, abTestGroup,
          analyticsContext, AnalyticsManager.Action.INSTALL, false, isInApkfyContext)
    }
  }

  private fun mapDownloadAction(downloadAction: DownloadStatusModel.Action): InstallType {
    return when (downloadAction) {
      DownloadStatusModel.Action.DOWNGRADE -> InstallType.DOWNGRADE
      DownloadStatusModel.Action.INSTALL -> InstallType.INSTALL
      DownloadStatusModel.Action.UPDATE -> InstallType.UPDATE
      DownloadStatusModel.Action.MIGRATE, DownloadStatusModel.Action.OPEN -> throw IllegalStateException(
          "Mapping an invalid download action " + downloadAction.name)
    }
  }

  private fun getOrigin(action: Int): Origin? {
    return when (action) {
      RoomDownload.ACTION_INSTALL -> Origin.INSTALL
      RoomDownload.ACTION_UPDATE -> Origin.UPDATE
      RoomDownload.ACTION_DOWNGRADE -> Origin.DOWNGRADE
      else -> Origin.INSTALL
    }
  }

  fun parseDownloadAction(action: DownloadStatusModel.Action): Int {
    val downloadAction: Int
    downloadAction = when (action) {
      DownloadStatusModel.Action.INSTALL -> RoomDownload.ACTION_INSTALL
      DownloadStatusModel.Action.UPDATE -> RoomDownload.ACTION_UPDATE
      DownloadStatusModel.Action.DOWNGRADE -> RoomDownload.ACTION_DOWNGRADE
      DownloadStatusModel.Action.MIGRATE -> RoomDownload.ACTION_DOWNGRADE
      else -> throw IllegalArgumentException("Invalid action $action")
    }
    return downloadAction
  }
}