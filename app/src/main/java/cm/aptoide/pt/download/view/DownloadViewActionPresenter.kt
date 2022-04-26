package cm.aptoide.pt.download.view

import cm.aptoide.analytics.AnalyticsManager
import cm.aptoide.pt.aab.DynamicSplit
import cm.aptoide.pt.aab.DynamicSplitsManager
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
import cm.aptoide.pt.presenter.ActionPresenter
import cm.aptoide.pt.presenter.View
import hu.akarnokd.rxjava.interop.RxJavaInterop
import rx.Completable
import rx.Observable
import rx.Scheduler

/**
 * This presenter is only responsible for handling download actions.
 * This means that whoever uses this is responsible for updating the download status correctly.
 *
 * This is useful in RecyclerView scenarios, where it does not make sense to tie each download view
 * to a new presenter and where updating a view means actually updating a list of downloads.
 *
 * To update a view based on a Download object, consider observing [DownloadStatusModel] using
 * [DownloadStatusManager] and rendering [Download] to views using [DownloadViewStatusHelper]
 */
open class DownloadViewActionPresenter(
  private val installManager: InstallManager,
  private val moPubAdsManager: MoPubAdsManager,
  private val permissionManager: PermissionManager,
  private val appcMigrationManager: AppcMigrationManager,
  private val downloadDialogProvider: DownloadDialogProvider,
  private val downloadNavigator: DownloadNavigator,
  private val permissionService: PermissionService,
  private val ioScheduler: Scheduler,
  private val viewScheduler: Scheduler,
  private val downloadFactory: DownloadFactory,
  private val downloadAnalytics: DownloadAnalytics,
  private val installAnalytics: InstallAnalytics,
  private val notificationAnalytics: NotificationAnalytics,
  private val crashReport: CrashReport,
  private val dynamicSplitsManager: DynamicSplitsManager,
  private val splitAnalyticsMapper: SplitAnalyticsMapper
) :
  ActionPresenter<DownloadClick>() {

  private lateinit var analyticsContext: DownloadAnalytics.AppContext
  private var isInApkfyContext = false
  private var editorsChoicePosition: String? = null

  fun setContextParams(
    context: DownloadAnalytics.AppContext, isApkfy: Boolean,
    editorsChoicePosition: String?
  ) {
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
          .skipWhile { event -> event.action == DownloadEvent.GENERIC_ERROR || event.action == DownloadEvent.OUT_OF_SPACE_ERROR }
          .flatMapCompletable { event ->
            when (event.action) {
              DownloadEvent.INSTALL -> installApp(event)
              DownloadEvent.RESUME -> resumeDownload(event)
              DownloadEvent.PAUSE -> pauseDownload(event)
              DownloadEvent.CANCEL -> cancelDownload(event)
              DownloadEvent.GENERIC_ERROR -> downloadDialogProvider.showGenericError()
              DownloadEvent.OUT_OF_SPACE_ERROR -> handleOutOfSpaceError(event)
            }
          }
          .retry()
      }
      .compose(lifecycleView.bindUntilEvent(View.LifecycleEvent.DESTROY))
      .subscribe({}, { err -> crashReport.log(err) })

  }

  private fun handleOutOfSpaceError(downloadClick: DownloadClick): Completable {
    return Completable.fromAction {
      downloadAnalytics.sendNotEnoughSpaceError(downloadClick.download.md5)
    }.andThen(
      downloadNavigator.openOutOfSpaceDialog(
        downloadClick.download.size,
        downloadClick.download.packageName
      )
    )
      .andThen(downloadNavigator.outOfSpaceDialogResult()
        .filter { result -> result.clearedSuccessfully }).first().toCompletable()
      .andThen(resumeDownload(downloadClick))
      .doOnError { t: Throwable? ->
        t?.printStackTrace()
      }
  }

  private fun installApp(downloadClick: DownloadClick): Completable {
    when (downloadClick.download.downloadModel?.action) {
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
        return downloadNavigator.openApp(downloadClick.download.packageName)
      }
      else -> {
        return Completable.complete()
      }
    }
  }

  private fun downgradeApp(
    download: Download,
    status: OfferResponseStatus
  ): Completable {
    return downloadDialogProvider.showDowngradeDialog()
      .filter { downgrade -> downgrade }
      .doOnNext { downloadDialogProvider.showDowngradingSnackBar() }
      .flatMapCompletable { downloadApp(download, status) }
      .toCompletable()
  }

  private fun downloadApp(
    download: Download,
    status: OfferResponseStatus
  ): Completable {
    return Observable.defer {
      if (installManager.showWarning()) {
        return@defer downloadDialogProvider.showRootInstallWarningPopup()
          .doOnNext { answer -> installManager.rootInstallAllowed(answer) }
          .map { download }
      }
      return@defer Observable.just(download)
    }
      .observeOn(viewScheduler)
      .flatMap {
        permissionManager.requestDownloadAccessWithWifiBypass(
          permissionService,
          download.size
        )
          .flatMap { permissionManager.requestExternalStoragePermission(permissionService) }
          .flatMapSingle {
            RxJavaInterop.toV1Single(dynamicSplitsManager.getAppSplitsByMd5(download.md5))
          }
          .observeOn(ioScheduler)
          .flatMapCompletable {
            createDownload(download, status, it.dynamicSplitsList)
              .doOnNext { roomDownload ->
                setupDownloadEvents(
                  roomDownload, download.appId,
                  download.downloadModel!!.action, status, download.storeName,
                  download.malware.rank.name
                )
                if (DownloadStatusModel.Action.MIGRATE == download.downloadModel.action) {
                  installAnalytics.uninstallStarted(
                    download.packageName,
                    AnalyticsManager.Action.INSTALL,
                    analyticsContext
                  )
                  appcMigrationManager.addMigrationCandidate(download.packageName)
                }
              }
              .flatMapCompletable { download -> installManager.install(download) }
              .toCompletable()
          }

      }
      .toCompletable()

  }

  private fun createDownload(
    download: Download,
    offerResponseStatus: OfferResponseStatus,
    dynamicSplitsList: List<DynamicSplit>
  ): Observable<RoomDownload> {
    return Observable.just(dynamicSplitsList)
      .flatMap {
        Observable.just(
          downloadFactory.create(
            parseDownloadAction(download.downloadModel!!.action),
            download.appName, download.packageName, download.md5, download.icon,
            download.versionName, download.versionCode, download.path,
            download.pathAlt,
            download.obb, download.hasAdvertising || download.hasBilling,
            download.size,
            download.splits, download.requiredSplits,
            download.malware.rank.toString(), download.storeName, download.oemId,
            dynamicSplitsList
          )
        )
      }
      .doOnError { throwable ->
        if (throwable is InvalidAppException) {
          downloadAnalytics.sendAppNotValidError(
            download.packageName,
            download.versionCode,
            mapDownloadAction(download.downloadModel!!.action), offerResponseStatus,
            download.downloadModel.action == DownloadStatusModel.Action.MIGRATE,
            download.splits.isNotEmpty(),
            download.hasAdvertising || download.hasBilling,
            download.malware
              .rank
              .toString(),
            download.storeName, isInApkfyContext, throwable, download.obb != null,
            splitAnalyticsMapper.getSplitTypesAsString(
              download.splits.isNotEmpty(),
              dynamicSplitsList
            )
          )
        }
      }
  }

  private fun resumeDownload(downloadClick: DownloadClick): Completable {
    return installManager.getDownload(downloadClick.download.md5)
      .flatMap { download ->
        moPubAdsManager.adsVisibilityStatus
          .doOnSuccess { status ->
            val dl = downloadClick.download
            setupDownloadEvents(
              download, dl.appId, dl.downloadModel!!.action, status,
              dl.storeName, dl.malware.rank.name
            )
          }.map { download }
      }
      .doOnError { throwable -> throwable.printStackTrace() }
      .flatMapCompletable { download -> installManager.install(download) }
  }

  private fun pauseDownload(downloadClick: DownloadClick): Completable {
    return Completable.fromAction {
      downloadAnalytics.downloadInteractEvent(downloadClick.download.packageName, "pause")
    }.andThen(installManager.pauseInstall(downloadClick.download.md5))
  }

  private fun cancelDownload(downloadClick: DownloadClick): Completable {
    return Completable.fromAction {
      downloadAnalytics.downloadInteractEvent(downloadClick.download.packageName, "cancel")
    }.andThen(
      installManager.cancelInstall(
        downloadClick.download.md5,
        downloadClick.download.packageName, downloadClick.download.versionCode
      )
    )
  }


  fun setupDownloadEvents(
    download: RoomDownload,
    appId: Long,
    downloadAction: DownloadStatusModel.Action,
    offerResponseStatus: OfferResponseStatus?,
    storeName: String?, malwareRank: String
  ) {
    val campaignId = notificationAnalytics.getCampaignId(download.packageName, appId)
    val abTestGroup = notificationAnalytics.getAbTestingGroup(download.packageName, appId)
    installAnalytics.installStarted(
      download.packageName, download.versionCode,
      AnalyticsManager.Action.INSTALL, analyticsContext,
      getOrigin(download.action), campaignId, abTestGroup,
      downloadAction == DownloadStatusModel.Action.MIGRATE,
      download.hasAppc(), download.hasSplits(), offerResponseStatus.toString(), malwareRank,
      storeName, isInApkfyContext, download.hasObbs(),
      splitAnalyticsMapper.getSplitTypesAsString(download.splits)
    )
    if (DownloadStatusModel.Action.MIGRATE == downloadAction) {
      downloadAnalytics.migrationClicked(
        download.md5, download.versionCode, download.packageName,
        malwareRank, editorsChoicePosition, InstallType.UPDATE_TO_APPC,
        AnalyticsManager.Action.INSTALL, offerResponseStatus, download.hasAppc(),
        download.hasSplits(), storeName,
        isInApkfyContext, download.hasObbs(),
        splitAnalyticsMapper.getSplitTypesAsString(download.splits)
      )
      downloadAnalytics.downloadStartEvent(
        download, campaignId, abTestGroup,
        analyticsContext, AnalyticsManager.Action.INSTALL, true, isInApkfyContext
      )
    } else {
      downloadAnalytics.installClicked(
        download.md5, download.versionCode, download.packageName,
        malwareRank, editorsChoicePosition, mapDownloadAction(downloadAction),
        AnalyticsManager.Action.INSTALL, offerResponseStatus, download.hasAppc(),
        download.hasSplits(), storeName, isInApkfyContext, download.hasObbs(),
        splitAnalyticsMapper.getSplitTypesAsString(download.splits)
      )
      downloadAnalytics.downloadStartEvent(
        download, campaignId, abTestGroup,
        analyticsContext, AnalyticsManager.Action.INSTALL, false, isInApkfyContext
      )
    }
  }

  private fun mapDownloadAction(downloadAction: DownloadStatusModel.Action): InstallType {
    return when (downloadAction) {
      DownloadStatusModel.Action.DOWNGRADE -> InstallType.DOWNGRADE
      DownloadStatusModel.Action.INSTALL -> InstallType.INSTALL
      DownloadStatusModel.Action.UPDATE -> InstallType.UPDATE
      DownloadStatusModel.Action.MIGRATE, DownloadStatusModel.Action.OPEN -> throw IllegalStateException(
        "Mapping an invalid download action " + downloadAction.name
      )
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