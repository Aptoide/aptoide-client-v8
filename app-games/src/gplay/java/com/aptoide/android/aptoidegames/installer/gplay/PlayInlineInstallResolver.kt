package com.aptoide.android.aptoidegames.installer.gplay

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import cm.aptoide.pt.download_view.presentation.InlineInstallResolver
import cm.aptoide.pt.extensions.compatVersionCode
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.install_manager.InstallManager
import cm.aptoide.pt.install_manager.Task
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

  // Packages whose install went through the fallback details overlay instead of the
  // Catalog Access half-sheet - reported apart, the two flows differ commercially
  private val overlayFallbacks: MutableSet<String> = ConcurrentHashMap.newKeySet()

  override suspend fun resolveInlineInstall(app: App): Intent? {
    if (app.packageName in abortedInlineInstalls) {
      log("${app.packageName}: previous inline attempt aborted -> regular install path")
      return null
    }
    val catalogToken = catalogTokenRepository.getCatalogToken(app.packageName)
    if (catalogToken == null) {
      log("${app.packageName}: no catalog token -> regular install path")
      return null
    }

    overlayFallbacks.remove(app.packageName)
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
   * Play Store >= 52.6 rejects the documented [PLAY_DEEP_LINK_URL] deep link before even
   * validating the token ("PITH: called from wrong URI" in Finsky logs), killing the
   * transparent half-sheet activity instantly. The fallback launches Play's public app
   * details overlay instead - a referral to a regular Play install, not a Catalog Access
   * distribution. Deliberately WITHOUT the catalog token: this route hard-aborts on a
   * token it cannot validate, while its absence is the verified-working form.
   */
  override suspend fun resolveFallbackInstall(app: App): Intent? = buildPlayIntent(
    url = "$PLAY_DETAILS_URL?id=${app.packageName}&referrer=$PLAY_REFERRER",
    catalogToken = null,
  ).takeIf {
    (it.resolveActivity(context.packageManager) != null).also { resolves ->
      if (resolves) {
        overlayFallbacks.add(app.packageName)
        log("${app.packageName}: fallback details-overlay intent ready")
      } else {
        log("${app.packageName}: Play Store does not resolve the details overlay")
      }
    }
  }

  private fun buildPlayIntent(
    url: String,
    catalogToken: ByteArray?,
  ): Intent = Intent(Intent.ACTION_VIEW).apply {
    setPackage(PLAY_STORE_PACKAGE)
    data = url.toUri()
    putExtra("overlay", true)
    putExtra("callerId", context.packageName)
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
    installAnalytics.sendInlineInstallCanceledEvent(app, installMethodFor(app.packageName))
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
    installAnalytics.sendInlineInstallCompletedEvent(app, installMethodFor(app.packageName))
    notificationsBuilder.showInstallationStateNotification(
      packageName = app.packageName,
      appDetails = appDetails,
      appIcon = appIcon,
      state = Task.State.Completed,
      size = 0
    )
    ongoingInstalls.remove(app.packageName)
    overlayFallbacks.remove(app.packageName)
  }

  private fun installMethodFor(packageName: String): String =
    if (packageName in overlayFallbacks) {
      InstallAnalytics.METHOD_PLAY_OVERLAY
    } else {
      InstallAnalytics.METHOD_PLAY_INLINE
    }

  private fun log(message: String) = Timber.tag(INLINE_INSTALL_TAG).d(message)

  private companion object {
    const val PLAY_STORE_PACKAGE = "com.android.vending"
    const val PLAY_DEEP_LINK_URL = "https://play.google.com/d"

    // Play's public app details overlay - the fallback when the Catalog Access
    // deep link above is rejected (see resolveFallbackInstall)
    const val PLAY_DETAILS_URL = "https://play.google.com/store/apps/details"

    // Delivered by Play to the installed app via the Install Referrer API,
    // attributing the install to Aptoide
    const val PLAY_REFERRER = "aptoidegames-play"

    const val INLINE_INSTALL_TAG = "InlineInstall"
  }
}
