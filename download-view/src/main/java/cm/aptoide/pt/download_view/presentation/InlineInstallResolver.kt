package cm.aptoide.pt.download_view.presentation

import android.content.Intent
import cm.aptoide.pt.feature_apps.data.App

/**
 * Decides whether installing [App] should be diverted to an external install flow
 * (e.g. Google Play inline installs on the Play-distributed build) instead of the
 * regular InstallManager download+install path.
 *
 * The binding is optional (see [cm.aptoide.pt.download_view.di.InlineInstallResolverModule]):
 * apps that never divert installs don't bind an implementation.
 */
interface InlineInstallResolver {

  /**
   * Returns a ready-to-launch [Intent] to install [app] externally,
   * or null to proceed with the regular install path.
   */
  suspend fun resolveInlineInstall(app: App): Intent?

  /** Called when the external install UI was launched for [app]. */
  fun onInlineInstallStarted(app: App) {}

  /** Called when the external install UI was closed without [app] being installed. */
  fun onInlineInstallCanceled(app: App) {}

  /**
   * Returns true while an external install for [packageName] is still ongoing,
   * so the UI state can be restored after the view model is recreated.
   */
  fun isInlineInstallOngoing(packageName: String): Boolean = false
}
