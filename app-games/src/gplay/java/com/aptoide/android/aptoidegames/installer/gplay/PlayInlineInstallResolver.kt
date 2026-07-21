package com.aptoide.android.aptoidegames.installer.gplay

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.net.toUri
import cm.aptoide.pt.download_view.presentation.InlineInstallResolver
import cm.aptoide.pt.extensions.compatVersionCode
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.install_manager.InstallManager
import cm.aptoide.pt.install_manager.Task
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.installer.AppDetailsUseCase
import com.aptoide.android.aptoidegames.installer.analytics.InstallAnalytics
import com.aptoide.android.aptoidegames.installer.notifications.ImageDownloader
import com.aptoide.android.aptoidegames.installer.notifications.InstallerNotificationsBuilder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Diverts installs to the Google Play inline install half-sheet
 * (https://developer.android.com/distribute/marketing-tools/inline-installs-3pas).
 *
 * Requires this app to be installed from Play, a fresh catalog token for the target app
 * and a Play Store version that resolves the deep link. When any requirement is not met
 * it returns null and the regular download+install path is used.
 *
 * While Play installs there is no task nor progress on our side, so ongoing installs are
 * tracked here to survive view model recreation and to drive an indeterminate progress
 * notification until the package lands (observed through [InstallManager]).
 */
@Singleton
class PlayInlineInstallResolver @Inject constructor(
  @ApplicationContext private val context: Context,
  private val catalogTokenRepository: CatalogTokenRepository,
  private val installManager: InstallManager,
  private val appDetailsUseCase: AppDetailsUseCase,
  private val imageDownloader: ImageDownloader,
  private val notificationsBuilder: InstallerNotificationsBuilder,
  private val installAnalytics: InstallAnalytics,
) : InlineInstallResolver {

  private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
  private val ongoingInstalls = ConcurrentHashMap<String, Job>()

  // Play gives no error signal inside the half-sheet (e.g. a rejected token), so once an
  // inline attempt ends without an install, further attempts for that app in this session
  // go through the regular install path instead of retrying inline.
  private val abortedInlineInstalls: MutableSet<String> = ConcurrentHashMap.newKeySet()

  override suspend fun resolveInlineInstall(app: App): Intent? {
    if (app.packageName in abortedInlineInstalls) {
      log("${app.packageName}: previous inline attempt aborted -> regular install path")
      return null
    }
    if (!isInstalledFromPlay()) {
      if (IS_DEV_MODE) {
        // Dev-mode builds are adb/IDE-installed, so the Play install source guard is bypassed
        // to allow exercising the inline flow locally (incl. devRelease); prod is unaffected
        log("${app.packageName}: not installed from Play, bypassing guard on dev build")
      } else {
        log("${app.packageName}: app store not installed from Play -> regular install path")
        return null
      }
    }
    val catalogToken = catalogTokenRepository.getCatalogToken(app.packageName)
    if (catalogToken == null) {
      log("${app.packageName}: no catalog token -> regular install path")
      return null
    }

    return Intent(Intent.ACTION_VIEW).apply {
      setPackage(PLAY_STORE_PACKAGE)
      data = "$PLAY_DEEP_LINK_URL?id=${app.packageName}&referrer=$PLAY_REFERRER".toUri()
      putExtra("overlay", true)
      putExtra("callerId", context.packageName)
      putExtra("catalog_token", catalogToken)
    }.takeIf {
      (it.resolveActivity(context.packageManager) != null).also { resolves ->
        log(
          if (resolves) "${app.packageName}: inline install intent ready (callerId=${context.packageName})"
          else "${app.packageName}: Play Store does not resolve the deep link -> regular install path"
        )
      }
    }
  }

  override fun onInlineInstallStarted(app: App) {
    ongoingInstalls.computeIfAbsent(app.packageName) {
      scope.launch { trackInstallation(app) }
    }
  }

  override fun onInlineInstallCanceled(app: App) {
    log("${app.packageName}: inline install canceled, next attempts use the regular install path")
    abortedInlineInstalls.add(app.packageName)
    ongoingInstalls.remove(app.packageName)?.cancel()
    installAnalytics.sendInlineInstallCanceledEvent(app)
    notificationsBuilder.showInstallationStateNotification(
      packageName = app.packageName,
      appDetails = null,
      appIcon = null,
      state = Task.State.Canceled,
      size = 0
    )
  }

  override fun isInlineInstallOngoing(packageName: String): Boolean =
    ongoingInstalls.containsKey(packageName)

  private suspend fun trackInstallation(app: App) {
    log("${app.packageName}: tracking Play installation, showing indeterminate notification")
    runCatching { appDetailsUseCase.setAppDetails(app) }
    val appInstaller = installManager.getApp(app.packageName)
    val appDetails = runCatching { appDetailsUseCase.getAppDetails(appInstaller) }.getOrNull()
    val appIcon = imageDownloader.downloadImageFrom(appDetails?.iconUrl ?: app.icon)

    notificationsBuilder.showExternalInstallingNotification(
      packageName = app.packageName,
      appDetails = appDetails,
      appIcon = appIcon
    )

    appInstaller.packageInfoFlow.first {
      it != null && it.compatVersionCode >= app.versionCode
    }

    log("${app.packageName}: package installed by Play, showing installed notification")
    installAnalytics.sendInlineInstallCompletedEvent(app)
    notificationsBuilder.showInstallationStateNotification(
      packageName = app.packageName,
      appDetails = appDetails,
      appIcon = appIcon,
      state = Task.State.Completed,
      size = 0
    )
    ongoingInstalls.remove(app.packageName)
  }

  private fun isInstalledFromPlay(): Boolean = runCatching {
    val installerPackageName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      context.packageManager.getInstallSourceInfo(context.packageName).installingPackageName
    } else {
      @Suppress("DEPRECATION")
      context.packageManager.getInstallerPackageName(context.packageName)
    }
    installerPackageName == PLAY_STORE_PACKAGE
  }.getOrDefault(false)

  private fun log(message: String) = Timber.tag(INLINE_INSTALL_TAG).d(message)

  private companion object {
    const val PLAY_STORE_PACKAGE = "com.android.vending"
    const val PLAY_DEEP_LINK_URL = "https://play.google.com/d"

    // Delivered by Play to the installed app via the Install Referrer API,
    // attributing the install to Aptoide
    const val PLAY_REFERRER = "aptoidegames-play"

    const val INLINE_INSTALL_TAG = "InlineInstall"
    val IS_DEV_MODE = BuildConfig.FLAVOR_mode == "dev"
  }
}
