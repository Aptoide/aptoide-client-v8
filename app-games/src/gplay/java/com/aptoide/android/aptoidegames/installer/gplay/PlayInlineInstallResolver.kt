package com.aptoide.android.aptoidegames.installer.gplay

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import cm.aptoide.pt.download_view.presentation.InlineInstallResolver
import cm.aptoide.pt.extensions.compatVersionCode
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.install_manager.InstallManager
import cm.aptoide.pt.install_manager.Task
import com.aptoide.android.aptoidegames.apkfy.isFreeFire
import com.aptoide.android.aptoidegames.apkfy.isRoblox
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
 * Only exists in the gplay flavor — the direct flavor binds no [InlineInstallResolver].
 *
 * Requires a fresh catalog token for the target app and a Play Store version that resolves
 * the deep link. When any requirement is not met it returns null and the regular
 * download+install path is used.
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

  // Packages whose whole inline ladder was rejected without showing any UI; further
  // attempts for them in this session go straight to the regular install path.
  private val abortedInlineInstalls: MutableSet<String> = ConcurrentHashMap.newKeySet()

  override suspend fun resolveInlineInstall(app: App): Intent? {
    if (app.installsThroughDetailsOverlay()) return resolveDetailsOverlay(app)

    if (app.packageName in abortedInlineInstalls) {
      log("${app.packageName}: previous inline attempt aborted -> regular install path")
      return null
    }
    val catalogToken = catalogTokenRepository.getCatalogToken(app.packageName)
    if (catalogToken == null) {
      log("${app.packageName}: no catalog token -> regular install path")
      return null
    }

    return buildPlayIntent(
      url = "$PLAY_DEEP_LINK_URL?id=${app.packageName}&referrer=$PLAY_REFERRER",
      catalogToken = catalogToken,
    ).takeIf {
      (it.resolveActivity(context.packageManager) != null).also { resolves ->
        log(
          if (resolves) "${app.packageName}: inline install intent ready (callerId=${context.packageName})"
          else "${app.packageName}: Play Store does not resolve the deep link -> regular install path"
        )
      }
    }
  }

  /**
   * Free Fire, Free Fire MAX and Roblox are distributed exclusively through Play's app
   * details overlay on this build: a token-less referral to a regular Play install,
   * reached through the apkfy flow and kept out of the searchable catalog. They must
   * never go through Aptoide's own install path here (see [allowsRegularFallback]) nor
   * through the Catalog Access route - they are not part of the catalog export.
   */
  private fun resolveDetailsOverlay(app: App): Intent? = buildPlayIntent(
    url = "$PLAY_DETAILS_URL?id=${app.packageName}&referrer=$PLAY_REFERRER",
    catalogToken = null,
  ).takeIf {
    (it.resolveActivity(context.packageManager) != null).also { resolves ->
      log(
        if (resolves) "${app.packageName}: details-overlay install intent ready"
        else "${app.packageName}: Play Store does not resolve the details overlay"
      )
    }
  }

  // Overlay-only titles never fall back to Aptoide's own install path - a failed or
  // dismissed overlay is a canceled install, and retries go through the overlay again
  override fun allowsRegularFallback(app: App): Boolean = !app.installsThroughDetailsOverlay()

  // No resolveFallbackInstall override: CATALOG apps use Google's documented method or
  // Aptoide's own install path, never Play's public details overlay - developers who
  // opted out of Catalog Access must not surface through us via the generic overlay.

  private fun buildPlayIntent(
    url: String,
    catalogToken: String?,
  ): Intent = Intent(Intent.ACTION_VIEW).apply {
    setPackage(PLAY_STORE_PACKAGE)
    data = url.toUri()
    putExtra("overlay", true)
    putExtra("callerId", context.packageName)
    // A String, NOT byte[]: Finsky reads a String extra; a byte[] reads back as null and
    // kills the half-sheet instantly (misleading "PITH: called from wrong URI" log).
    // Google's docs showed byte[] until they corrected them in 2026-08.
    catalogToken?.let { putExtra("catalog_token", it) }
  }

  override fun onInlineInstallStarted(app: App) {
    ongoingInstalls.computeIfAbsent(app.packageName) {
      scope.launch { trackInstallation(app) }
    }
  }

  override fun onInlineInstallCanceled(app: App) {
    // The user dismissed a sheet that visibly worked, so inline stays available for
    // future attempts - only invisible ladder rejections abort it for the session
    log("${app.packageName}: inline install canceled by the user")
    ongoingInstalls.remove(app.packageName)?.cancel()
    installAnalytics.sendInlineInstallCanceledEvent(app, app.installMethod())
    notificationsBuilder.showInstallationStateNotification(
      packageName = app.packageName,
      appDetails = null,
      appIcon = null,
      state = Task.State.Canceled,
      size = 0
    )
  }

  override fun onInlineInstallUnavailable(app: App) {
    log(
      "${app.packageName}: inline install unavailable, " +
        "this session's next attempts use the regular install path"
    )
    abortedInlineInstalls.add(app.packageName)
    ongoingInstalls.remove(app.packageName)?.cancel()
    // Clears the indeterminate installing notification; the regular install path taking
    // over reuses the same per-package notification right away
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
    installAnalytics.sendInlineInstallCompletedEvent(app, app.installMethod())
    notificationsBuilder.showInstallationStateNotification(
      packageName = app.packageName,
      appDetails = appDetails,
      appIcon = appIcon,
      state = Task.State.Completed,
      size = 0
    )
    ongoingInstalls.remove(app.packageName)
  }

  private fun log(message: String) = Timber.tag(INLINE_INSTALL_TAG).d(message)

  private companion object {
    const val PLAY_STORE_PACKAGE = "com.android.vending"
    const val PLAY_DEEP_LINK_URL = "https://play.google.com/d"

    // Play's public app details overlay - see resolveDetailsOverlay
    const val PLAY_DETAILS_URL = "https://play.google.com/store/apps/details"

    // Delivered by Play to the installed app via the Install Referrer API,
    // attributing the install to Aptoide
    const val PLAY_REFERRER = "aptoidegames-play"

    const val INLINE_INSTALL_TAG = "InlineInstall"
  }
}

fun App.installsThroughDetailsOverlay(): Boolean = isFreeFire() || isRoblox()

private fun App.installMethod(): String = if (installsThroughDetailsOverlay()) {
  InstallAnalytics.METHOD_PLAY_OVERLAY
} else {
  InstallAnalytics.METHOD_PLAY_INLINE
}
